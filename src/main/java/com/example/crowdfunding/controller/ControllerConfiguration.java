package com.example.crowdfunding.controller;

import com.example.crowdfunding.controller.chain.CrowdfundingCellCreator;
import com.example.crowdfunding.controller.chain.Pledger;
import com.example.crowdfunding.controller.chain.TransactionParameters;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class ControllerConfiguration implements WebMvcConfigurer {
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
    public TransactionParameters chequeCellTransactionParameters() {
        TransactionParameters configuration = new TransactionParameters();
        configuration.setCkbRpcApi(ckbRpcApi());
        configuration.setCkbIndexerApi(ckbIndexerApi());
        configuration.setSender(env.getProperty("platform.address"));
        configuration.setPrivateKey(env.getProperty("platform.privateKey"));
        configuration.setCellDeps(parseCellDep(env.getProperty("pledge.cell.lock.cellDep")));
        configuration.setCodeHash(env.getProperty("pledge.cell.lock.codeHash"));
        configuration.setHashType(Script.HashType.valueOf(env.getProperty("pledge.cell.lock.hashType")));
        return configuration;
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

    @Bean
    public Pledger pledger() {
        Pledger pledger = new Pledger();
        pledger.setParameters(chequeCellTransactionParameters());
        return pledger;
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 1允许任何域名使用
        corsConfiguration.addAllowedOrigin("*");
        // 2允许任何头
        corsConfiguration.addAllowedHeader("*");
        // 3.允许ajax异步请求带cookie信息
        corsConfiguration.setAllowCredentials(true);
        // 4允许任何方法（post、get等）
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "responseType", "Authorization"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    private List<CellDep> parseCellDep(String s) {
        List<CellDep> cellDeps = new ArrayList<>();
        String[] cellDepsStr = s.split("\n");
        for (int i = 0; i < cellDepsStr.length; i++) {
            String str = cellDepsStr[i];
            str = str.substring(1, str.length() - 1);
            String[] cellDepStr = str.split(",");
            cellDeps.add(new CellDep(new OutPoint(
                    Numeric.hexStringToByteArray(cellDepStr[0]),
                    Integer.parseInt(cellDepStr[1])),
                    CellDep.DepType.valueOf(cellDepStr[2])));
        }
        return cellDeps;
    }
}
