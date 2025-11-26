package br.com.sankhya.jaco.notaCompra.utils;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Classe utilitária para operações CRUD (Create, Read, Update, Delete) em entidades do sistema
 * usando a API JapeWrapper. Fornece uma interface fluente e type-safe para operações de banco de dados.
 *
 * <p>Exemplo de uso:</p>
 * <pre>
 * // Inserção
 * new DynamicCrudWraperHelper.Builder("TABELA", OperationType.INSERT)
 *     .withFieldsToSet(Map.of("CAMPO1", valor1, "CAMPO2", valor2))
 *     .build()
 *     .execute();
 *
 * // Atualização por PK
 * new DynamicCrudWraperHelper.Builder("TABELA", OperationType.UPDATE_BY_PK)
 *     .withPrimaryKey(BigDecimal.valueOf(123))
 *     .withFieldsToSet(Map.of("CAMPO1", novoValor))
 *     .build()
 *     .execute();
 * // Atualização por PK
 *     try {
 *     new DynamicCrudWraperHelper.Builder("AD_CENTRALPARAMESTLOG",
 *             DynamicCrudWraperHelper.OperationType.UPDATE_BY_PK)
 *             .withPrimaryKey(codEmpPK, codLogNew) // Valores da PK composta
 *             .withFieldsToSet(params) // Campos para atualizar
 *             .build()
 *             .execute();
 * } catch (Exception e) {
 *     helperLog.error(showLogs, "Erro ao atualizar AD_CENTRALPARAMESTLOG", e);
 *     throw new Exception("Falha ao atualizar log central", e);
 * }
 *
 * //Atualizando pelo VO
 * try {
 *     // 1. Primeiro obtemos o VO existente
 *     EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
 *     DynamicVO voExistente = (DynamicVO) dwfFacade.findEntityByPK(
 *         "AD_CENTRALPARAMESTLOG",
 *         new Object[]{codEmpPK, codLogNew} // PK composta: CODEMPSAI, CODLOG
 *     );
 *
 *     // 2. Atualizamos os campos necessários no VO
 *     for (Map.Entry<String, Object> entry : params.entrySet()) {
 *         voExistente.setProperty(entry.getKey(), entry.getValue());
 *     }
 *
 *     // 3. Executamos o UPDATE
 *     new DynamicCrudWraperHelper.Builder("AD_CENTRALPARAMESTLOG",
 *             DynamicCrudWraperHelper.OperationType.UPDATE)
 *             .updateVO(voExistente)
 *             .build()
 *             .execute();
 *
 * } catch (Exception e) {
 *     helperLog.error(showLogs, "Erro ao atualizar AD_CENTRALPARAMESTLOG", e);
 *     throw new Exception("Falha ao atualizar log central", e);
 * }
 *
 * </pre>
 */

public class DynamicCrudWraperHelper {
    private final String entityName;
    private final OperationType operationType;
    private final Object[] primaryKeys;

    private final BigDecimal primaryKey;
    private final Map<String, Object> fieldsToSet;
    private final String deleteCriteria;
    private final Object[] deleteCriteriaParams;

    private final DynamicVO updateVO;

    /**
     * Tipos de operações suportadas pelo helper
     */
    public enum OperationType {
        /**
         * Operação de inserção de novo registro
         */
        INSERT,
        /**
         * Operação de atualização por chave primária
         */
        UPDATE_BY_PK,
        /**
         * Operação de exclusão por chave primária
         */
        DELETE_BY_PK,
        /**
         * Operação de exclusão por critérios customizados
         */
        DELETE_BY_CRITERIA,
        /**
         * Operação de atualização usando um VO existente
         */
        UPDATE
    }

    /**
     * Construtor privado usado pelo Builder
     *
     * @param builder Instância do Builder com parâmetros configurados
     */

    private DynamicCrudWraperHelper(Builder builder) {
        this.entityName = builder.entityName;
        this.operationType = builder.operationType;
        this.primaryKeys = builder.primaryKeys;
        this.primaryKey = builder.primaryKey;
        this.fieldsToSet = builder.fieldsToSet;
        this.deleteCriteria = builder.deleteCriteria;
        this.deleteCriteriaParams = builder.deleteCriteriaParams;
        this.updateVO = builder.updateVO;
    }

    /**
     * Executa a operação CRUD configurada
     *
     * @return O objeto resultante da operação (VO para insert/update, null para delete)
     * @throws Exception Se ocorrer qualquer erro durante a execução
     */

    public Object execute() throws Exception {
        SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            JapeWrapper dao = JapeFactory.dao(entityName);

            switch (operationType) {
                case INSERT:
                    FluidCreateVO create = dao.create();
                    applyFieldSets(create);
                    return create.save();

                case UPDATE_BY_PK:
                    if ((primaryKey == null) && (primaryKeys == null || primaryKeys.length == 0)) {
                        throw new IllegalArgumentException("Chave primária é obrigatória para atualizar a tabela");
                    }

                    FluidUpdateVO update;
                    if (primaryKey != null) {
                        // PK simples
                        update = dao.prepareToUpdateByPK(primaryKey);
                    } else {
                        // PK composta
                        update = dao.prepareToUpdateByPK(primaryKeys);
                    }

                    applyFieldSets(update);
                    update.update();
                    return update;

                case UPDATE:
                    if (updateVO == null) {
                        throw new IllegalArgumentException("Necessário um criterio para efetuar o Update");
                    }

                    FluidUpdateVO updateVO1 = (FluidUpdateVO) dao.prepareToUpdate(updateVO);


                    updateVO1.update();
                    return updateVO1;

                case DELETE_BY_PK:
                    if (primaryKey == null) {
                        throw new IllegalArgumentException("Chave primaria é obrigatória para deletar o registro");
                    }
                    dao.delete(primaryKey);
                    return null;

                case DELETE_BY_CRITERIA:
                    if (deleteCriteria == null) {
                        throw new IllegalArgumentException("Parametros não informados para deleção!");
                    }
                    dao.deleteByCriteria(deleteCriteria, deleteCriteriaParams);
                    return null;

                default:
                    throw new UnsupportedOperationException("Operation type not supported");
            }
        } finally {
            JapeSession.close(hnd);
        }
    }

    /**
     * Aplica os valores dos campos configurados em um objeto FluidVO
     *
     * @param fluidObject Objeto FluidVO (create ou update) que receberá os valores
     */

    private void applyFieldSets(Object fluidObject) {
        if (fieldsToSet != null) {
            for (Map.Entry<String, Object> entry : fieldsToSet.entrySet()) {
                if (fluidObject instanceof FluidCreateVO) {
                    ((FluidCreateVO) fluidObject).set(entry.getKey(), entry.getValue());
                } else if (fluidObject instanceof FluidUpdateVO) {
                    ((FluidUpdateVO) fluidObject).set(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     * Builder para construção imutável de DynamicCrudWraperHelper
     */

    public static class Builder {
        private final String entityName;
        private final OperationType operationType;

        private BigDecimal primaryKey;
        private Map<String, Object> fieldsToSet;
        private String deleteCriteria;
        private Object[] deleteCriteriaParams;
        private DynamicVO updateVO;
        private Object[] primaryKeys;

        /**
         * Cria um novo Builder para a operação
         *
         * @param entityName    Nome da entidade/tabela
         * @param operationType Tipo de operação a ser executada
         */

        public Builder(String entityName, OperationType operationType) {
            this.entityName = entityName;
            this.operationType = operationType;
        }

        /**
         * Define a chave primária (para operações que usam PK simples)
         *
         * @param primaryKey Valor da chave primária
         * @return O próprio builder para method chaining
         */

        public Builder withPrimaryKey(BigDecimal primaryKey) {
            this.primaryKey = primaryKey;
            return this;
        }

        /**
         * Define os campos e valores para operações de insert/update
         *
         * @param fieldsToSet Mapa com nomes de campos e valores
         * @return O próprio builder para method chaining
         */

        public Builder withFieldsToSet(Map<String, Object> fieldsToSet) {
            this.fieldsToSet = fieldsToSet;
            return this;
        }

        /**
         * Define critérios para deleção customizada
         *
         * @param criteria Critério SQL (com ? para parâmetros)
         * @param params   Valores dos parâmetros
         * @return O próprio builder para method chaining
         */

        public Builder withDeleteCriteria(String criteria, Object... params) {
            this.deleteCriteria = criteria;
            this.deleteCriteriaParams = params;
            return this;
        }

        /**
         * Define um VO existente para operações de update
         *
         * @param updateVO VO com os dados a serem atualizados
         * @return O próprio builder para method chaining
         */

        public Builder updateVO(DynamicVO updateVO) {
            this.updateVO = updateVO;

            return this;
        }

        /**
         * Define múltiplas chaves primárias (para PK composta)
         *
         * @param primaryKeys Valores das chaves primárias
         * @return O próprio builder para method chaining
         */

        public Builder withPrimaryKey(Object... primaryKeys) {
            this.primaryKeys = primaryKeys;
            return this;
        }

        /**
         * Constrói a instância de DynamicCrudWraperHelper
         *
         * @return Nova instância configurada
         */


        public DynamicCrudWraperHelper build() {
            return new DynamicCrudWraperHelper(this);
        }
    }
}