package br.com.sankhya.jaco.cotacao.schedule;

import br.com.sankhya.jaco.cotacao.dao.GeraEmailCotacaoDao;
import br.com.sankhya.jaco.integracao.helper.DynamicFinderWrapperHelper;
import br.com.sankhya.jaco.integracao.helper.HelperLog;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import org.cuckoo.core.ScheduledAction;
import org.cuckoo.core.ScheduledActionContext;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Collection;

public class EnviaEmailAviso24HsCotacaoSchedule implements ScheduledAction {

	private final HelperLog helperLog = new HelperLog();
	private final boolean showLogs = true;

	@Override
	public void onTime(ScheduledActionContext context) {

		JapeSession.SessionHandle session = null;
		GeraEmailCotacaoDao geraEmailCotacaoDao = new GeraEmailCotacaoDao();

		try {

			helperLog.debug(showLogs,
					"Iniciando o envio de emails para as cotacoes pendentes de aprovacao EnviaEmailAviso24HsCotacaoSchedule");

			helperLog.info(showLogs, "Iniciando o envio de emails para as cotacoes pendentes de aprovacao");
			avisoAprovadoresCotacao(session, geraEmailCotacaoDao);
			helperLog.info(showLogs,
					"Finalizando o envio de emails para as liberacoes de limites pendentes de liberacao");

			avisoLiberacoesLimitesPendentes(session, geraEmailCotacaoDao);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void avisoLiberacoesLimitesPendentes(JapeSession.SessionHandle session,
			GeraEmailCotacaoDao geraEmailCotacaoDao) {

		try {

			final ResultSet resultSetCotacao = findLiberacoesPendentes();

			if (resultSetCotacao != null && resultSetCotacao.next()) {
				do {
					try {
						session = JapeSession.open();
						session.setCanTimeout(false);

						session.execWithTX(() -> {
							BigDecimal numCotacao = resultSetCotacao.getBigDecimal("NUMCOTACAO");
							String email = resultSetCotacao.getString("EMAIL");

							geraEmailCotacaoDao.geraEmailCotacaoSemAnexo(numCotacao, email,
									"<div style=\"background-color:#fdecea; padding:16px 20px; border-left:6px solid #f44336; border-radius:8px; margin:20px 0; font-size:14px; color:#a94442; line-height:1.5;\">\n"
											+ "    <strong>Atenção:</strong> A cotação não foi aprovada no período de <strong>24 horas</strong> a contar da data de sua emissão.\n"
											+ "</div>",
									helperLog, showLogs);
						});
					} catch (Exception e) {
						helperLog.error(showLogs, "Erro durante execução do schedule", e);
					} finally {
						if (session != null) {
							JapeSession.close(session);
						}
					}
				} while (resultSetCotacao.next());
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void avisoAprovadoresCotacao(JapeSession.SessionHandle session, GeraEmailCotacaoDao geraEmailCotacaoDao)
			throws Exception {

		try {

			final ResultSet resultSetCotacao = findCotacoesPendentesDeAprovacao();

			final Collection<DynamicVO> usuarios = geraEmailCotacaoDao.responseUsersLiberadores();

			while (resultSetCotacao != null && resultSetCotacao.next()) {

				try {
					session = JapeSession.open();
					session.setCanTimeout(false);

					session.execWithTX(() -> {

						BigDecimal numCotacao = resultSetCotacao.getBigDecimal("NUMCOTACAO");

						geraEmailCotacaoDao.geraEmailCotacaoComAnexo(numCotacao, BigDecimal.ZERO,
								"<div style=\"background-color:#fdecea; padding:16px 20px; border-left:6px solid #f44336; border-radius:8px; margin:20px 0; font-size:14px; color:#a94442; line-height:1.5;\">\n"
										+ "    <strong>Atenção:</strong> A cotação não foi aprovada no período de <strong>24 horas</strong> a contar da data de sua emissão.\n"
										+ "</div>",
								usuarios, helperLog, showLogs);

					});

				} catch (Exception e) {
					helperLog.error(showLogs, "Erro durante execução do schedule", e);
				} finally {
					if (session != null) {
						JapeSession.close(session);
					}
				}

			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private ResultSet findLiberacoesPendentes() throws Exception {
		helperLog.info(showLogs, "Iniciando a busca de cotacoes pendentes de aprovacao");

		try {

			DynamicFinderWrapperHelper.Builder builder = new DynamicFinderWrapperHelper.Builder(null,
					DynamicFinderWrapperHelper.FinderOperationType.NATIVE_SQL)
					.withNativeSql(queryCotacoesPendentesDeAprovacao());

			DynamicFinderWrapperHelper.NativeQueryResult queryResult = (DynamicFinderWrapperHelper.NativeQueryResult) builder
					.build().execute();

			ResultSet resultSet = queryResult.getResultSet();

			return resultSet;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private ResultSet findCotacoesPendentesDeAprovacao() throws Exception {

		helperLog.info(showLogs, "Iniciando a busca de cotacoes pendentes de aprovacao");

		try {

			DynamicFinderWrapperHelper.Builder builder = new DynamicFinderWrapperHelper.Builder(null,
					DynamicFinderWrapperHelper.FinderOperationType.NATIVE_SQL)
					.withNativeSql(queryLiberacoesPendentes());

			DynamicFinderWrapperHelper.NativeQueryResult queryResult = (DynamicFinderWrapperHelper.NativeQueryResult) builder
					.build().execute();

			ResultSet resultSet = queryResult.getResultSet();

			return resultSet;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	String queryCotacoesPendentesDeAprovacao() {
		return "WITH USUARIOS AS\n" +
				" (SELECT TO_CHAR(TRUNC(SYSDATE + 1), 'DD/MM/YYYY') DATA_PK, U.EMAIL\n" +
				"    FROM TSIUSU U\n" +
				"   WHERE U.AD_LIBERACOTACAO = 'S')\n" +
				"SELECT USU.EMAIL,\n" +
				"       T.NUMCOTACAO,\n" +
				"       TO_CHAR(T.DTALTER, 'DD/MM/YYYY') AS DATA_HORA\n" +
				"  FROM TGFCOT T\n" +
				" INNER JOIN USUARIOS USU\n" +
				"    ON USU.DATA_PK = TO_CHAR(T.DTALTER, 'DD/MM/YYYY')\n" +
				" WHERE T.SITUACAO = 'A'\n" +
				"      \n" +
				"   AND TRUNC(T.DTALTER, 'HH24') = TRUNC(SYSDATE + 1, 'HH24')";
	}

	String queryLiberacoesPendentes() {
		return "SELECT \n"
				+ "    TO_NUMBER(T.NUCHAVE) AS NUMCOTACAO, \n"
				+ "    U.EMAIL\n"
				+ "FROM TSILIB T\n"
				+ "INNER JOIN TSIUSU U \n" + "       ON U.CODUSU = T.CODUSULIB\n"
				+ "WHERE T.DHLIB IS NULL\n"
				+ "  AND T.REPROVADO = 'N'\n" + "  AND T.EVENTO in( 1006,1007) \n"
				+ "  AND TRUNC(T.DHSOLICIT, 'HH24') = TRUNC(SYSDATE + 1, 'HH24')";
	}
}
