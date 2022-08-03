package com.example.crowdfunding.controller.chain;

import com.example.crowdfunding.model.Backer;
import com.example.crowdfunding.model.OnChainCell;
import com.example.crowdfunding.model.Project;
import org.nervos.ckb.Network;
import org.nervos.ckb.crypto.Blake2b;
import org.nervos.ckb.crypto.secp256k1.ECKeyPair;
import org.nervos.ckb.service.GsonFactory;
import org.nervos.ckb.sign.ScriptGroup;
import org.nervos.ckb.sign.TransactionSigner;
import org.nervos.ckb.sign.TransactionWithScriptGroups;
import org.nervos.ckb.sign.signer.Secp256k1Blake160SighashAllSigner;
import org.nervos.ckb.transaction.CkbTransactionBuilder;
import org.nervos.ckb.type.*;
import org.nervos.ckb.utils.Numeric;
import org.nervos.ckb.utils.Utils;
import org.nervos.ckb.utils.address.Address;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Aggregator {
    private TransactionParameters parameters;

    public void setParameters(TransactionParameters parameters) {
        this.parameters = parameters;
    }

    public List<OnChainCell> aggregate2(List<Backer> backers, Project project, Long amount) {
        Network network = Network.TESTNET;
        Transaction tx = new Transaction();
        List<ScriptGroup> scriptGroups = new ArrayList<>();
        List<OnChainCell> onChainCells = new ArrayList<>();
        tx.cellDeps.addAll(parameters.getCellDeps());
        tx.cellDeps.add(new CellDep(project.getCrowdfundingCell(), CellDep.DepType.CODE));
        // Total
        System.out.println("amount: " + amount);
        long total = 0;
        for (int i = 0; i < backers.size(); i++) {
            Backer backer = backers.get(i);
            tx.inputs.add(new CellInput(backer.getCurrentPledgedCell().getOutPoint(), 0));
            tx.witnesses.add(new WitnessArgs(65).pack().toByteArray());
            ScriptGroup scriptGroup = new ScriptGroup();
            scriptGroup.setScript(backer.getCurrentPledgedCell().getOutput().lock);
            scriptGroup.setGroupType(ScriptType.LOCK);
            scriptGroup.getInputIndices().add(i);
            scriptGroups.add(scriptGroup);
            System.out.println("pledged " + i + " " + backer.getCurrentPledgedCell().getOutput().capacity);
            total += backer.getCurrentPledgedCell().getOutput().capacity;
        }
        System.out.println("total: " + total);
        // avoid difference error
        if (total - amount > 10) {
            for (Backer backer: backers) {
                OnChainCell oldCell = backer.getCurrentPledgedCell();
                CellOutput output = new CellOutput();
                output.lock = oldCell.getOutput().lock;
                output.type = oldCell.getOutput().type;
                // fee 500
                output.capacity = (long) (oldCell.getOutput().capacity * ((total - amount) / (float) total) - 500);
                byte[] outputData = new byte[0];
                tx.outputs.add(output);
                tx.outputsData.add(outputData);

                OnChainCell newCell = new OnChainCell();
                newCell.setOutput(output);
                newCell.setOutputData(outputData);
                onChainCells.add(newCell);
            }
        }

        Address receiverAddress = Address.decode(project.getCreatorAddress());
        CellOutput receiverOutput = new CellOutput(amount, receiverAddress.getScript());
        tx.outputs.add(receiverOutput);
        tx.outputsData.add(new byte[0]);

        TransactionWithScriptGroups txWithGroups = new TransactionWithScriptGroups();
        txWithGroups.setTxView(tx);
        txWithGroups.setScriptGroups(scriptGroups);

        System.out.println(GsonFactory.create().toJson(txWithGroups));


        ECKeyPair keyPair = ECKeyPair.create(parameters.getPrivateKey());
        for (int i = 0; i < scriptGroups.size(); i++) {
            Secp256k1Blake160SighashAllSigner.getInstance()
                    .signScriptGroup(tx, scriptGroups.get(i), keyPair);
        }
        //
        //        Secp256k1Blake160SighashAllSigner.getInstance()
        //                .signScriptGroup()
        //
        //
        //        TransactionSigner x = TransactionSigner.getInstance(network)
        //                .registerLockScriptSigner(parameters.getCodeHash(), new CrowdfundingCellScriptSigner())
        //                                      .registerLockScriptSigner()
        //
        //        Set<Integer> ss = x.signTransaction(txWithGroups, parameters.getPrivateKey());
        //        for (Integer ii : ss) {
        //            System.out.println(ii);
        //        }
        System.out.println("private key: " + parameters.getPrivateKey());
        System.out.println("code hash: " + parameters.getCodeHash());

        System.out.println("----txWithGroups----");
        System.out.println(GsonFactory.create().toJson(txWithGroups));

        byte[] txHash = new byte[0];
        try {
            txHash = parameters.getCkbRpcApi().sendTransaction(txWithGroups.getTxView());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < onChainCells.size(); i++) {
            onChainCells.get(i).setOutPoint(new OutPoint(txHash, i));
        }
        return onChainCells;
    }


    public List<OnChainCell> aggregate(List<Backer> backers, Project project, Long amount) {
        Network network = Network.TESTNET;

        CkbTransactionBuilder builder = new CkbTransactionBuilder(null, network);
        builder.addCellDeps(parameters.getCellDeps());
        builder.addCellDep(new CellDep(project.getCrowdfundingCell(), CellDep.DepType.CODE));

        List<OnChainCell> onChainCells = new ArrayList<>();
        // Total
        long total = 0;
        for (Backer backer: backers) {
            builder.addInput(newTransactionInput(backer.getCurrentPledgedCell()));
            total += backer.getCurrentPledgedCell().getOutput().capacity;
        }

        // avoid difference error
        if (total - amount > 10) {
            for (Backer backer: backers) {
                OnChainCell oldCell = backer.getCurrentPledgedCell();
                CellOutput output = new CellOutput();
                output.lock = oldCell.getOutput().lock;
                output.type = oldCell.getOutput().type;
                // fee 500
                output.capacity = oldCell.getOutput().capacity * (total - amount) / total - 500;
                byte[] outputData = new byte[0];
                builder.addOutput(output, outputData);

                OnChainCell newCell = new OnChainCell();
                newCell.setOutput(output);
                newCell.setOutputData(outputData);
                onChainCells.add(newCell);
            }
        }

        builder.addOutput(project.getCreatorAddress(), amount);
        builder.setFeeRate(1000);

        TransactionWithScriptGroups txWithGroups = builder.build();

        TransactionSigner.getInstance(network)
                .registerLockScriptSigner(parameters.getCodeHash(), new CrowdfundingCellScriptSigner())
                .signTransaction(txWithGroups, parameters.getPrivateKey());

        System.out.println("----txWithGroups----");
        System.out.println(GsonFactory.create().toJson(txWithGroups));

        byte[] txHash = new byte[0];
        //        try {
        //            txHash = parameters.getCkbRpcApi().sendTransaction(txWithGroups.getTxView());
        //        } catch (IOException e) {
        //            throw new RuntimeException(e);
        //        }
        for (int i = 0; i < onChainCells.size(); i++) {
            onChainCells.get(i).setOutPoint(new OutPoint(txHash, i));
        }
        return onChainCells;
    }

    private Address getAddress(String privateKey) {
        ECKeyPair keyPair = ECKeyPair.create(privateKey);
        Script lock = Script.generateSecp256K1Blake160SignhashAllScript(keyPair);
        return new Address(lock, Network.TESTNET);
    }

    private CellOutput newOutput(Backer backer, Project project) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(Blake2b.digest(project.toData()));
            Script receiverLock = Address.decode(parameters.getSender()).getScript();
            byte[] hash = receiverLock.computeHash();
            out.write(Arrays.copyOfRange(hash, 0, 20));

            Script senderLock = backer.getAddressType().getScript();
            hash = senderLock.computeHash();
            Arrays.copyOfRange(hash, 0, 20);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CellOutput output = new CellOutput();
        output.lock = new Script(Numeric.hexStringToByteArray(parameters.getCodeHash()), out.toByteArray(), parameters.getHashType());
        output.capacity = Utils.ckbToShannon(backer.getPledgedCKB());
        return output;
    }

    private TransactionInput newTransactionInput(OnChainCell onChainCell) {
        TransactionInput input = new TransactionInput(new CellInput(onChainCell.getOutPoint(), 0), onChainCell.getOutput());
        input.outputData = onChainCell.getOutputData();
        return input;
    }
}
