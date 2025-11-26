package br.com.sankhya.jaco.notaCompra.utils;

import br.com.sankhya.jape.EntityFacade;



import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.JdbcUtils;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;


/**
 * Classe utilitária para executar operações de banco de dados de forma dinâmica
 * Suporta operações JAPE (findByPK, find, findOne) e SQL nativo
 * 
 * EXEMPLOS DE USO:
 * 
 * 1. FIND_BY_PK - Buscar registro por chave primária:
 *    DynamicFinderWrapperHelper helper = new DynamicFinderWrapperHelper.Builder(
 *        "Produto", DynamicFinderWrapperHelper.FinderOperationType.FIND_BY_PK)
 *        .withPrimaryKey(1000)
 *        .build();
 *    Object resultado = helper.execute();
 * 
 * 2. FIND_COLLECTION - Buscar múltiplos registros:
 *    DynamicFinderWrapperHelper helper = new DynamicFinderWrapperHelper.Builder(
 *        "CabecalhoNota", DynamicFinderWrapperHelper.FinderOperationType.FIND_COLLECTION)
 *        .withQueryCriteria("NUNOTA = ? AND CODTIPOPER = ?", 12345, 1000)
 *        .build();
 *    Object resultado = helper.execute();
 * 
 * 3. FIND_ONE - Buscar um único registro:
 *    DynamicFinderWrapperHelper helper = new DynamicFinderWrapperHelper.Builder(
 *        "Parceiro", DynamicFinderWrapperHelper.FinderOperationType.FIND_ONE)
 *        .withQueryCriteria("CODCLI = ?", 5000)
 *        .build();
 *    Object resultado = helper.execute();
 * 
 * 4. NATIVE_SQL - Consulta SQL nativa com parâmetros nomeados:
 *    DynamicFinderWrapperHelper helper = new DynamicFinderWrapperHelper.Builder(
 *        "NativeQuery", DynamicFinderWrapperHelper.FinderOperationType.NATIVE_SQL)
 *        .withNativeSql("SELECT CODPROD, DESCRPROD FROM TGFPRO WHERE CODPROD = :codProd AND AD_ATIVO = :ativo")
 *        .withNamedParameter("codProd", 1000)
 *        .withNamedParameter("ativo", "S")
 *        .withQueryTimeout(30)
 *        .build();
 *    NativeQueryResult result = (NativeQueryResult) helper.execute();
 *    try (result) {
 *        ResultSet rs = result.getResultSet();
 *        while (rs.next()) {
 *            // Processar resultados
 *        }
 *    }
 * 
 * 5. NATIVE_UPDATE - Executar UPDATE/DELETE/INSERT:
 *    DynamicFinderWrapperHelper helper = new DynamicFinderWrapperHelper.Builder(
 *        "NativeUpdate", DynamicFinderWrapperHelper.FinderOperationType.NATIVE_UPDATE)
 *        .withNativeSql("UPDATE TGFPRO SET DESCRPROD = :descricao WHERE CODPROD = :codProd")
 *        .withNamedParameter("descricao", "Novo Produto")
 *        .withNamedParameter("codProd", 1000)
 *        .build();
 *    NativeQueryResult result = (NativeQueryResult) helper.execute();
 *    boolean sucesso = result.isResponseUpdate();
 */

/**
 * Builder para construção flexível das operações
 * 
 * EXEMPLOS COMPLETOS:
 * 
 * Exemplo 1: Buscar produto por código
 * new Builder("Produto", FinderOperationType.FIND_BY_PK)
 *     .withPrimaryKey(1000)
 *     .build()
 *     .execute();
 * 
 * Exemplo 2: Buscar notas fiscais com filtro complexo
 * new Builder("CabecalhoNota", FinderOperationType.FIND_COLLECTION)
 *     .withQueryCriteria("NUNOTA IN (SELECT NUNOTA FROM TGFCAB WHERE DTNEG >= ?)", 
 *         new java.util.Date())
 *     .build()
 *     .execute();
 * 
 * Exemplo 3: Consulta nativa com join entre tabelas
 * new Builder("NativeQuery", FinderOperationType.NATIVE_SQL)
 *     .withNativeSql("SELECT C.NUNOTA, P.CODPROD, P.DESCRPROD FROM TGFCAB C JOIN TGFITE I ON C.NUNOTA = I.NUNOTA JOIN TGFPRO P ON I.CODPROD = P.CODPROD WHERE C.NUNOTA = :nunota")
 *     .withNamedParameter("nunota", 12345)
 *     .build()
 *     .execute();
 */
public class DynamicFinderWrapperHelper {
    private final String entityName;
    private final FinderOperationType operationType;
    private Object[] primaryKeys;
    private String queryCriteria;
    private Object[] queryParams;
    private String nativeSql;
    private Map<String, Object> namedParameters;
    private Integer queryTimeout;

    public enum FinderOperationType {
        FIND_BY_PK, FIND_COLLECTION, FIND_ONE, NATIVE_SQL, NATIVE_UPDATE
    }

    private DynamicFinderWrapperHelper(Builder builder) {
        this.entityName = builder.entityName;
        this.operationType = builder.operationType;

        this.queryCriteria = builder.queryCriteria;
        this.queryParams = builder.queryParams;
        this.nativeSql = builder.nativeSql;
        this.namedParameters = builder.namedParameters;
        this.queryTimeout = builder.queryTimeout;
        this.primaryKeys = builder.primaryKeys;
    }

    /**

public class DynamicFinderWrapperHelper {
    private final String entityName;
    private final FinderOperationType operationType;
    private Object[] primaryKeys;
    private String queryCriteria;
    private Object[] queryParams;
    private String nativeSql;
    private Map<String, Object> namedParameters;
    private Integer queryTimeout;

    public enum FinderOperationType {
        FIND_BY_PK, FIND_COLLECTION, FIND_ONE, NATIVE_SQL, NATIVE_UPDATE
    }

    private DynamicFinderWrapperHelper(Builder builder) {
        this.entityName = builder.entityName;
        this.operationType = builder.operationType;

        this.queryCriteria = builder.queryCriteria;
        this.queryParams = builder.queryParams;
        this.nativeSql = builder.nativeSql;
        this.namedParameters = builder.namedParameters;
        this.queryTimeout = builder.queryTimeout;
        this.primaryKeys = builder.primaryKeys;
    }

    /**
     * Executa a operação conforme configurada no builder
     */
    public Object execute() throws Exception {
        switch (operationType) {
            case NATIVE_SQL:
            case NATIVE_UPDATE:
                return executeNativeQuery();
            default:
                return executeJapeOperation();
        }
    }

    private Object executeJapeOperation() throws Exception {
        SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JapeWrapper dao = JapeFactory.dao(entityName);

            switch (operationType) {
                case FIND_BY_PK:
                    if (primaryKeys == null) {
                        throw new IllegalArgumentException("Primary key is required for FIND_BY_PK operation");
                    }


                    return dao.findByPK(primaryKeys);


                case FIND_COLLECTION:
                    if (queryCriteria == null) {
                        throw new IllegalArgumentException("Query criteria is required for FIND_COLLECTION operation");
                    }
                    return dao.find(queryCriteria, queryParams);

                case FIND_ONE:
                    if (queryCriteria == null) {
                        throw new IllegalArgumentException("Query criteria is required for FIND_ONE operation");
                    }
                    return dao.findOne(queryCriteria, queryParams);

                default:
                    throw new UnsupportedOperationException("Operation type not supported");
            }
        } finally {
            if (hnd != null) {
                JapeSession.close(hnd);
            }
        }
    }

    private NativeQueryResult executeNativeQuery() throws Exception {
        JdbcWrapper jdbc = null;
        NativeSql sql = null;
        ResultSet rset = null;
        boolean responseUpdate = false;

        try {
            EntityFacade entity = EntityFacadeFactory.getDWFFacade();
            jdbc = entity.getJdbcWrapper();
            jdbc.openSession();

            // Configura timeout se definido
            if (queryTimeout != null) {
                jdbc.setQueryTimeout(queryTimeout);
            }

            sql = new NativeSql(jdbc);
            sql.appendSql(nativeSql);

            // Seta parâmetros nomeados
            if (namedParameters != null) {
                for (Map.Entry<String, Object> entry : namedParameters.entrySet()) {
                    sql.setNamedParameter(entry.getKey(), entry.getValue());
                }
            }

            // Executa conforme o tipo de operação
            if (operationType == FinderOperationType.NATIVE_UPDATE) {
                responseUpdate = sql.executeUpdate();
            } else {
                rset = sql.executeQuery();
            }

            return new NativeQueryResult(rset, sql, jdbc, responseUpdate);

        } catch (Exception e) {
            // Garante o fechamento dos recursos em caso de erro
            JdbcUtils.closeResultSet(rset);
            NativeSql.releaseResources(sql);
            if (jdbc != null) {
                JdbcWrapper.closeSession(jdbc);
            }
            throw e;
        }
    }

    public static class NativeQueryResult implements AutoCloseable {
        private final ResultSet resultSet;
        private final NativeSql nativeSql;
        private final JdbcWrapper jdbcWrapper;
        private final boolean responseUpdate;

        public NativeQueryResult(ResultSet resultSet, NativeSql nativeSql,
                                 JdbcWrapper jdbcWrapper, boolean responseUpdate) {
            this.resultSet = resultSet;
            this.nativeSql = nativeSql;
            this.jdbcWrapper = jdbcWrapper;
            this.responseUpdate = responseUpdate;
        }

        public ResultSet getResultSet() {
            return resultSet;
        }

        public boolean isResponseUpdate() {
            return responseUpdate;
        }

        @Override
        public void close() {
            JdbcUtils.closeResultSet(resultSet);
            NativeSql.releaseResources(nativeSql);
            if (jdbcWrapper != null) {
                JdbcWrapper.closeSession(jdbcWrapper);
            }
        }
    }

    public static class Builder {
        private final String entityName;
        private final FinderOperationType operationType;
        private String queryCriteria;
        private Object[] queryParams;
        private String nativeSql;
        private Map<String, Object> namedParameters;
        private Integer queryTimeout;
        private Object[] primaryKeys;

        public Builder(String entityName, FinderOperationType operationType) {
            this.entityName = entityName;
            this.operationType = operationType;
            if (operationType == FinderOperationType.NATIVE_SQL ||
                    operationType == FinderOperationType.NATIVE_UPDATE) {
                this.namedParameters = new HashMap<>();
            }
        }

        public Builder withPrimaryKey(Object... primaryKeys) {
            this.primaryKeys = primaryKeys;
            return this;
        }

        public Builder withQueryCriteria(String criteria, Object... params) {
            this.queryCriteria = criteria;
            this.queryParams = params;
            return this;
        }

        public Builder withNativeSql(String sql) {
            if (operationType != FinderOperationType.NATIVE_SQL &&
                    operationType != FinderOperationType.NATIVE_UPDATE) {
                throw new IllegalStateException("Native SQL can only be used with NATIVE_SQL or NATIVE_UPDATE operation types");
            }
            this.nativeSql = sql;
            return this;
        }

        public Builder withNamedParameter(String name, Object value) {
            if (namedParameters == null) {
                namedParameters = new HashMap<>();
            }
            namedParameters.put(name, value);
            return this;
        }

        public Builder withQueryTimeout(int seconds) {
            this.queryTimeout = seconds;
            return this;
        }

        public DynamicFinderWrapperHelper build() {
            validate();
            return new DynamicFinderWrapperHelper(this);
        }

        private void validate() {
            if ((operationType == FinderOperationType.NATIVE_SQL ||
                    operationType == FinderOperationType.NATIVE_UPDATE) &&
                    nativeSql == null) {
                throw new IllegalStateException("Native SQL query is required for NATIVE_SQL/NATIVE_UPDATE operation types");
            }
        }


        @Override
        public String toString() {
            String sqlInterpolado = nativeSql;
            if (namedParameters != null) {
                for (Map.Entry<String, Object> entry : namedParameters.entrySet()) {
                    String valor = formatarValor(entry.getValue());
                    sqlInterpolado = sqlInterpolado.replace(":" + entry.getKey(), valor);
                }
            }
            return sqlInterpolado;
        }

        private String formatarValor(Object valor) {
            if (valor instanceof String || valor instanceof java.util.Date) {
                return "'" + valor.toString() + "'";
            }
            return valor != null ? valor.toString() : "null";
        }
    }


}