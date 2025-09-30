package br.com.sankhya.jaco.integracao.helper;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.JdbcUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class DynamicFinderWrapperHelper {
    private final String entityName;
    private final FinderOperationType operationType;
    private BigDecimal primaryKey;
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
        this.primaryKey = builder.primaryKey;
        this.queryCriteria = builder.queryCriteria;
        this.queryParams = builder.queryParams;
        this.nativeSql = builder.nativeSql;
        this.namedParameters = builder.namedParameters;
        this.queryTimeout = builder.queryTimeout;
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
                    if (primaryKey == null) {
                        throw new IllegalArgumentException("Primary key is required for FIND_BY_PK operation");
                    }
                    return dao.findByPK(primaryKey);

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
        private BigDecimal primaryKey;
        private String queryCriteria;
        private Object[] queryParams;
        private String nativeSql;
        private Map<String, Object> namedParameters;
        private Integer queryTimeout;

        public Builder(String entityName, FinderOperationType operationType) {
            this.entityName = entityName;
            this.operationType = operationType;
            if (operationType == FinderOperationType.NATIVE_SQL ||
                    operationType == FinderOperationType.NATIVE_UPDATE) {
                this.namedParameters = new HashMap<>();
            }
        }

        public Builder withPrimaryKey(BigDecimal primaryKey) {
            this.primaryKey = primaryKey;
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