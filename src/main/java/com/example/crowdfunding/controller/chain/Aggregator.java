package com.example.crowdfunding.controller.chain;

import com.example.crowdfunding.model.Backer;
import com.example.crowdfunding.model.OnChainCell;
import com.example.crowdfunding.model.Project;
import org.nervos.ckb.crypto.secp256k1.ECKeyPair;
import org.nervos.ckb.service.GsonFactory;
import org.nervos.ckb.sign.ScriptGroup;
import org.nervos.ckb.sign.TransactionWithScriptGroups;
import org.nervos.ckb.sign.signer.Secp256k1Blake160SighashAllSigner;
import org.nervos.ckb.type.*;
import org.nervos.ckb.utils.address.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Aggregator {
    private final static Logger logger = LoggerFactory.getLogger(Aggregator.class);
    private TransactionParameters parameters;

    public void setParameters(TransactionParameters parameters) {
        this.parameters = parameters;
    }

    public List<OnChainCell> aggregate(List<Backer> backers, Project project, Long amount) {
        Transaction tx = new Transaction();
        List<ScriptGroup> scriptGroups = new ArrayList<>();
        List<OnChainCell> onChainCells = new ArrayList<>();
        tx.cellDeps.addAll(parameters.getCellDeps());
        tx.cellDeps.add(new CellDep(project.getCrowdfundingCell(), CellDep.DepType.CODE));
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
            total += backer.getCurrentPledgedCell().getOutput().capacity;
        }
        // avoid difference error
        logger.info("total: " + total);
        logger.info("amount: " + amount);
        int ii = 0;
        if (total - amount > 10) {
            for (Backer backer: backers) {
                OnChainCell oldCell = backer.getCurrentPledgedCell();
                CellOutput output = new CellOutput();
                output.lock = oldCell.getOutput().lock;
                output.type = oldCell.getOutput().type;
                // fee 500
                output.capacity = (long) (oldCell.getOutput().capacity * ((total - amount) / (float) total) - 500);
                logger.info("index " + ii++ + " output capacity: " + oldCell.getOutput().capacity);
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
        logger.info("txWithGroups before signing");
        logger.info(GsonFactory.create().toJson(txWithGroups));

        ECKeyPair keyPair = ECKeyPair.create(parameters.getPrivateKey());
        for (int i = 0; i < scriptGroups.size(); i++) {
            Secp256k1Blake160SighashAllSigner.getInstance()
                    .signScriptGroup(tx, scriptGroups.get(i), keyPair);
        }
        logger.info("txWithGroups after signing");
        logger.info(GsonFactory.create().toJson(txWithGroups));

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
}
