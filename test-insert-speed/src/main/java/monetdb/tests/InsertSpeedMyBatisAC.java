package monetdb.tests;

import java.util.Date;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import monetdb.domain.monitordb.HistoryAI;
import monetdb.domain.monitordb.HistoryAIExample;
import monetdb.mapper.monitordb.HistoryAIMapper;

public class InsertSpeedMyBatisAC {

	public static final int ROW_COUNT = 10000;
	
	public static void main(String[] args) {
		ConfigurableApplicationContext dataCtx = new ClassPathXmlApplicationContext("data-access-context.xml");
		HistoryAIMapper mapper = dataCtx.getBean(HistoryAIMapper.class);
		//delete all.
		HistoryAIExample example = new HistoryAIExample();
		mapper.deleteByExample(example);
		long ts1 = System.currentTimeMillis();
		//insert.
		for (int i = 0; i < ROW_COUNT; ++i) {
			HistoryAI record = new HistoryAI();
			record.setId(i);
			record.setTs(new Date());
			record.setValue(Math.random());

			mapper.insert(record);
		}
		long ts2 = System.currentTimeMillis();
		System.out.println("rows/second: " + ROW_COUNT/((ts2 - ts1)/1000.0));
		dataCtx.close();
	}

}
