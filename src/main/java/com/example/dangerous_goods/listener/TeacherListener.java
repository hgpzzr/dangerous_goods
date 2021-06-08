package com.example.dangerous_goods.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.example.dangerous_goods.dao.TeacherMapper;
import com.example.dangerous_goods.form.AddTeacherForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/8 12:48
 */
public class TeacherListener extends AnalysisEventListener<AddTeacherForm> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TeacherListener.class);
	/**
	 * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
	 */
	private static final int BATCH_COUNT = 5;
	List<AddTeacherForm> list = new ArrayList<AddTeacherForm>();
	/**
	 * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
	 */
	private TeacherMapper teacherMapper;
	/**
	 * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
	 *
	 * @param teacherMapper
	 */
	public TeacherListener(TeacherMapper teacherMapper) {
		this.teacherMapper = teacherMapper;
	}
	/**
	 * 这个每一条数据解析都会来调用
	 *
	 * @param data
	 *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
	 * @param context
	 */
	@Override
	public void invoke(AddTeacherForm data, AnalysisContext context) {
		LOGGER.info("解析到一条数据:{}", JSON.toJSONString(data));
		list.add(data);
		// 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
		if (list.size() >= BATCH_COUNT) {
			saveData();
			// 存储完成清理 list
			list.clear();
		}
	}
	/**
	 * 所有数据解析完成了 都会来调用
	 *
	 * @param context
	 */
	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		// 这里也要保存数据，确保最后遗留的数据也存储到数据库
		saveData();
		LOGGER.info("所有数据解析完成！");
	}
	/**
	 * 加上存储数据库
	 */
	private void saveData() {
		LOGGER.info("{}条数据，开始存储数据库！", list.size());
		teacherMapper.batchInsert(list);
		LOGGER.info("存储数据库成功！");
	}
}
