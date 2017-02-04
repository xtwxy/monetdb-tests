package monetdb.tests;

import org.joda.time.DateTime;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class BulkInsert {

	public static int ID_COUNT = 200000;
	public static int COMMIT_COUNT = 500;

	public static void main(String[] args) {
		switch (args.length) {
		case 1:
			ID_COUNT = Integer.parseInt(args[0]);
			break;
		case 2:
			ID_COUNT = Integer.parseInt(args[0]);
			COMMIT_COUNT = Integer.parseInt(args[1]);
			break;
		default:
			break;
		}
		ConfigurableApplicationContext dataCtx = new ClassPathXmlApplicationContext("data-access-context.xml");
		JdbcTemplate jdbc = dataCtx.getBean(JdbcTemplate.class);
		// delete all.
		jdbc.execute("delete from history_ai");
		// insert.
		long ts1 = System.currentTimeMillis();
		jdbc.execute(new ConnectionCallback<Integer>() {

			@Override
			public Integer doInConnection(Connection con) throws SQLException, DataAccessException {
				con.setAutoCommit(false);
				PreparedStatement ps = con.prepareStatement("insert into history_ai(id, ts, value) values(?,?,?)");
				int commitCount = 0;

				for (int i = 0; i < ID_COUNT; ++i) {
					ps.setInt(1, i);
					DateTime stop = DateTime.now();
					DateTime start = stop.minusDays(365 * 1);
					System.out.println("id = " + i + ", start = " + start);
					do {

						Timestamp ts = new Timestamp(start.getMillis());
						ps.setTimestamp(2, ts);
						ps.setDouble(3, Math.random());
						ps.execute();
						start = start.plusMinutes(1);
						
						commitCount++;
						if (commitCount >= COMMIT_COUNT) {
							con.commit();
							commitCount = 0;
						}
					} while (start.isBefore(stop.getMillis()));
				}
				System.out.println("id = " + (ID_COUNT - 1) + ", start = " + DateTime.now());
				con.commit();
				return commitCount;
			}

		});
		long ts2 = System.currentTimeMillis();
		System.out.println("rows/second: " + ID_COUNT / ((ts2 - ts1) / 1000.0));
		dataCtx.close();
	}

}
