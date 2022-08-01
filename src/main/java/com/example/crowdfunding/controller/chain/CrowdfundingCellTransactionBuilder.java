package com.example.crowdfunding.controller.chain;

import org.nervos.ckb.Network;
import org.nervos.ckb.crypto.Blake2b;
import org.nervos.ckb.sign.TransactionWithScriptGroups;
import org.nervos.ckb.transaction.CkbTransactionBuilder;
import org.nervos.ckb.type.Transaction;
import org.nervos.ckb.type.TransactionInput;
import org.nervos.ckb.utils.MoleculeConverter;

import java.util.Iterator;

public class CrowdfundingCellTransactionBuilder extends CkbTransactionBuilder {

    public CrowdfundingCellTransactionBuilder(Iterator<TransactionInput> availableInputs, Network network) {
        super(availableInputs, network);
    }

    @Override
    public TransactionWithScriptGroups build(Object... contexts) {
        TransactionWithScriptGroups txWithGroup = super.build(contexts);
        Transaction tx = txWithGroup.getTxView();
        Blake2b blake2b = new Blake2b();
        blake2b.update(tx.inputs.get(0).pack().toByteArray());
        blake2b.update(MoleculeConverter.packUint64(0L).toByteArray());
        tx.outputs.get(0).type.args = blake2b.doFinal();
        return txWithGroup;
    }
}
