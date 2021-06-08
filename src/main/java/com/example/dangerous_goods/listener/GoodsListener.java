package com.example.dangerous_goods.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.example.dangerous_goods.dao.GoodsInfoMapper;
import com.example.dangerous_goods.dao.GoodsMapper;
import com.example.dangerous_goods.entity.Goods;
import com.example.dangerous_goods.entity.GoodsInfo;
import com.example.dangerous_goods.form.ExcelForm;
import com.example.dangerous_goods.utils.GenerateIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/2 11:36
 */

public class GoodsListener extends AnalysisEventListener<ExcelForm> {

	private static final Logger LOGGER = LoggerFactory.getLogger(GoodsListener.class);
	/**
	 * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
	 */
	private static final int BATCH_COUNT = 5;
	List<ExcelForm> list = new ArrayList<ExcelForm>();
	/**
	 * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
	 */
	private GoodsMapper goodsMapper;

	private GoodsInfoMapper goodsInfoMapper;

	/**
	 * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
	 *
	 * @param goodsMapper
	 */
	public GoodsListener(GoodsMapper goodsMapper) {
		this.goodsMapper = goodsMapper;
	}

	public GoodsListener(GoodsMapper goodsMapper, GoodsInfoMapper goodsInfoMapper) {
		this.goodsMapper = goodsMapper;
		this.goodsInfoMapper = goodsInfoMapper;
	}

	/**
	 * 这个每一条数据解析都会来调用
	 *
	 * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
	 * @param context
	 */
	@Override
	public void invoke(ExcelForm data, AnalysisContext context) {
		LOGGER.info("解析到一条数据:{}", JSON.toJSONString(data));
		// 赋值
		String goodsId = GenerateIdUtil.getGoodsId(goodsMapper);
		data.setGoodsId(goodsId);
		String goodsInfoId = GenerateIdUtil.getGoodsInfoId(goodsInfoMapper);
		data.setGoodsInfoId(goodsInfoId);
		data.setOverdueStatus(0);
		data.setVerifyStatus(2);
		data.setTakeOutStatus(0);
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
		List<Goods> goodsList = new ArrayList<>();
		List<GoodsInfo> goodsInfoList = new ArrayList<>();
		for (ExcelForm excelForm : list) {
			Goods goods = new Goods();
			GoodsInfo goodsInfo = new GoodsInfo();
			BeanUtils.copyProperties(excelForm,goods);
			BeanUtils.copyProperties(excelForm,goodsInfo);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				date = simpleDateFormat.parse(excelForm.getApplicationTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			goods.setApplicationTime(date);
			goodsList.add(goods);
			goodsInfoList.add(goodsInfo);
		}
		goodsMapper.batchInsert(goodsList);
		goodsInfoMapper.batchInsert(goodsInfoList);
		LOGGER.info("存储数据库成功！");
	}
}
