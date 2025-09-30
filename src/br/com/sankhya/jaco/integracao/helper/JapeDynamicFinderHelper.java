package br.com.sankhya.jaco.integracao.helper;

import java.util.Map;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

/**
 * Classe utilitária para consultas dinâmicas com Jape Exemplos :
 * 
 * 1 - Finder JapeDynamicFinderHelper finder = new
 * JapeDynamicFinderHelper.Builder(DynamicEntityNames.EMPRESA,
 * FinderType.FINDER_WRAPPER) .withQuery("this.CEP = ?", "38400102")
 * .withOrderBy("CODEMP") .withMaxResults(100) .build();
 * 
 * Collection<DynamicVO> empresas = (Collection<DynamicVO>) finder.execute();
 * 
 * 2 - Native SQL
 * 
 * Map<String, Object> params = new HashMap<>(); params.put("CHAVE",
 * "UTILIZAHTML5"); params.put("CODUSU", BigDecimal.ZERO);
 * 
 * JapeDynamicFinderHelper sqlFinder = new JapeDynamicFinderHelper.Builder(null,
 * FinderType.NATIVE_SQL) .withQuery("SELECT * FROM TSIPAR WHERE CHAVE = :CHAVE
 * AND CODUSU = :CODUSU") .withNamedParameters(params) .withMaxResults(-1)
 * .build();
 * 
 * ResultSet resultSet = (ResultSet) sqlFinder.execute();
 * 
 * 
 */
public class JapeDynamicFinderHelper {
	private final String entityName;
	private final FinderType finderType;
	private String query;
	private Object[] params;
	private Map<String, Object> namedParams;
	private String orderBy;
	private int maxResults = -1;

	public enum FinderType {
		FINDER_WRAPPER, NATIVE_SQL
	}

	private JapeDynamicFinderHelper(Builder builder) {
		this.entityName = builder.entityName;
		this.finderType = builder.finderType;
		this.query = builder.query;
		this.params = builder.params;
		this.namedParams = builder.namedParams;
		this.orderBy = builder.orderBy;
		this.maxResults = builder.maxResults;
	}

	public Object execute() throws Exception {
		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();
			EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

			switch (finderType) {
			case FINDER_WRAPPER:
				FinderWrapper finder = new FinderWrapper(entityName, query, params);
				if (orderBy != null) {
					finder.setOrderBy(orderBy);
				}
				finder.setMaxResults(maxResults);
				return dwfFacade.findByDynamicFinderAsVO(finder);

			case NATIVE_SQL:
				hnd.setFindersMaxRows(maxResults);
				NativeSql sql = new NativeSql(dwfFacade.getJdbcWrapper());
				sql.appendSql(query);

				if (namedParams != null) {
					for (Map.Entry<String, Object> entry : namedParams.entrySet()) {
						sql.setNamedParameter(entry.getKey(), entry.getValue());
					}
				}

				return sql.executeQuery();

			default:
				throw new UnsupportedOperationException("Finder type not supported");
			}
		} finally {
			JapeSession.close(hnd);
		}
	}

	public static class Builder {
		private final String entityName;
		private final FinderType finderType;
		private String query;
		private Object[] params;
		private Map<String, Object> namedParams;
		private String orderBy;
		private int maxResults = -1;

		public Builder(String entityName, FinderType finderType) {
			this.entityName = entityName;
			this.finderType = finderType;
		}

		public Builder withQuery(String query, Object... params) {
			this.query = query;
			this.params = params;
			return this;
		}

		public Builder withNamedParameters(Map<String, Object> namedParams) {
			this.namedParams = namedParams;
			return this;
		}

		public Builder withOrderBy(String orderBy) {
			this.orderBy = orderBy;
			return this;
		}

		public Builder withMaxResults(int maxResults) {
			this.maxResults = maxResults;
			return this;
		}

		public JapeDynamicFinderHelper build() {
			return new JapeDynamicFinderHelper(this);
		}
	}
}