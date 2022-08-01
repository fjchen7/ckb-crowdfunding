package com.example.crowdfunding.controller;

import com.example.crowdfunding.controller.chain.CrowdfundingCellCreator;
import org.nervos.ckb.CkbRpcApi;
import org.nervos.ckb.service.Api;
import org.nervos.ckb.type.CellDep;
import org.nervos.ckb.type.OutPoint;
import org.nervos.ckb.type.Script;
import org.nervos.ckb.utils.Numeric;
import org.nervos.indexer.CkbIndexerApi;
import org.nervos.indexer.DefaultIndexerApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ControllerConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ControllerConfiguration.class);
    @Autowired
    private Environment env;

    @Bean
    public HttpTraceRepository httpTraceRepository() {
        return new InMemoryHttpTraceRepository() {
            @Override
            public void add(HttpTrace trace) {
                log.info("Incoming request: " + trace.getRequest().getMethod()
                        + " " + trace.getRequest().getUri()
                        + " from " + trace.getRequest().getRemoteAddress()
                        + ", status: " + trace.getResponse().getStatus());
                super.add(trace);
            }
        };
    }

    @Bean
    public CkbRpcApi ckbRpcApi() {
        return new Api(env.getProperty("ckb.rpc.url"));
    }

    @Bean
    public CkbIndexerApi ckbIndexerApi() {
        return new DefaultIndexerApi(env.getProperty("ckb.indexer.rpc.url"), false);
    }

    @Bean
    public CrowdfundingCellCreator crowdfundingCellCreator() {
        CrowdfundingCellCreator creator = new CrowdfundingCellCreator();
        creator.setTypeCodeHash(env.getProperty("crowdfunding.cell.type.codeHash"));
        creator.setTypeHashType(Script.HashType.valueOf(env.getProperty("crowdfunding.cell.type.hashType")));
        creator.setCellDep(new CellDep(new OutPoint(
                Numeric.hexStringToByteArray(env.getProperty("crowdfunding.cell.type.cellDep.outPoint.txHash")),
                Integer.parseInt(env.getProperty("crowdfunding.cell.type.cellDep.outPoint.index"))),
                CellDep.DepType.valueOf(env.getProperty("crowdfunding.cell.type.cellDep.depType"))));
        creator.setCkbRpcApi(ckbRpcApi());
        creator.setCkbIndexerApi(ckbIndexerApi());
        creator.setSender(env.getProperty("platform.address"));
        creator.setPrivateKey(env.getProperty("platform.privateKey"));
        return creator;
    }
}
