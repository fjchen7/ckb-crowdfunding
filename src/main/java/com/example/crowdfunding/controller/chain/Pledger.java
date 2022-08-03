package com.example.crowdfunding.controller.chain;

import com.example.crowdfunding.model.Backer;
import com.example.crowdfunding.model.OnChainCell;
import com.example.crowdfunding.model.Project;
import org.nervos.ckb.Network;
import org.nervos.ckb.crypto.Blake2b;
import org.nervos.ckb.crypto.secp256k1.ECKeyPair;
import org.nervos.ckb.sign.TransactionSigner;
import org.nervos.ckb.sign.TransactionWithScriptGroups;
import org.nervos.ckb.transaction.CkbTransactionBuilder;
import org.nervos.ckb.type.CellOutput;
import org.nervos.ckb.type.OutPoint;
import org.nervos.ckb.type.Script;
import org.nervos.ckb.utils.Numeric;
import org.nervos.ckb.utils.Utils;
import org.nervos.ckb.utils.address.Address;
import org.nervos.indexer.InputIterator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Pledger {
    private TransactionParameters parameters;

    public void setParameters(TransactionParameters parameters) {
        this.parameters = parameters;
    }

    public OnChainCell pledge(Backer backer, Project project) {
        OnChainCell onChainCell = new OnChainCell();
        onChainCell.setOutput(newOutput(backer, project, parameters));
        onChainCell.setOutputData(new byte[0]);

        Network network = Network.TESTNET;
        InputIterator iterator = new InputIterator(parameters.getCkbIndexerApi());
        String addr = getAddress(backer.getPrivateKey()).encode();
        iterator.addSearchKey(addr);

        CkbTransactionBuilder builder = new CkbTransactionBuilder(iterator, network);
        builder.addCellDeps(parameters.getCellDeps());
        builder.addOutput(onChainCell.getOutput(), onChainCell.getOutputData());
        builder.setFeeRate(1000);
        builder.setChangeOutput(addr);

        TransactionWithScriptGroups txWithGroups = builder.build();

        TransactionSigner.getInstance(network)
                .signTransaction(txWithGroups, backer.getPrivateKey());
        byte[] txHash = new byte[0];
        try {
            txHash = parameters.getCkbRpcApi().sendTransaction(txWithGroups.getTxView());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        onChainCell.setOutPoint(new OutPoint(txHash, 0));
        return onChainCell;
    }

    private Address getAddress(String privateKey) {
        ECKeyPair keyPair = ECKeyPair.create(privateKey);
        Script lock = Script.generateSecp256K1Blake160SignhashAllScript(keyPair);
        return new Address(lock, Network.TESTNET);
    }

    public static CellOutput newOutput(Backer backer, Project project, TransactionParameters configuration) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(Blake2b.digest(project.toData()));
            Script receiverLock = Address.decode(configuration.getSender()).getScript();
            byte[] hash = receiverLock.computeHash();
            out.write(Arrays.copyOfRange(hash, 0, 20));

            Script senderLock = backer.getAddressType().getScript();
            hash = senderLock.computeHash();
            out.write(Arrays.copyOfRange(hash, 0, 20));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CellOutput output = new CellOutput();
        output.lock = new Script(Numeric.hexStringToByteArray(configuration.getCodeHash()), out.toByteArray(), configuration.getHashType());
        output.capacity = Utils.ckbToShannon(backer.getPledgedCKB());
        return output;
    }
}
