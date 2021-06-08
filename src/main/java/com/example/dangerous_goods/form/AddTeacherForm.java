package com.example.dangerous_goods.form;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.convert.DataSizeUnit;

import javax.validation.constraints.NotBlank;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/8 11:21
 */
@Data
public class AddTeacherForm {
	@ExcelProperty("姓名")
	private String name;

	@ExcelProperty("学号")
	private String jobId;

	@ExcelProperty("院系")
	private String college;
}
