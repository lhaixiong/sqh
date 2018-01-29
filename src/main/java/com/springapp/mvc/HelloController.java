package com.springapp.mvc;

import com.springapp.mvc.util.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Controller
public class HelloController {
	private static final Logger log= LoggerFactory.getLogger(HelloController.class);
	//工号-信息映射
	private static Map<String,String> CODE_INFO_MAP=new HashMap<String, String>();
	//奖品级别-中奖工号映射
	private static Map<String,Set<String>> BING_INFO_MAP=new TreeMap<String, Set<String>>();

	/**
	 * 查看抽奖结果
	 */
	@RequestMapping("/show")
	public String show(HttpServletRequest request,Map<String,Object> map){
		map.put("userInfo",getUserInfo());
		String bingoInfo=getBingoCodeByLevel();
		map.put("bingoInfo",bingoInfo);
		log.info("查看抽奖结果"+bingoInfo);
		return "show";
	}

	/**
	 * 保存本次抽奖结果
	 */
	@RequestMapping("/saveResult")
	@ResponseBody
	public Map<String,Object> saveResult(HttpServletRequest request,String prizeGrade,String thisCode){
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("success",true);
		if (!BING_INFO_MAP.containsKey(prizeGrade)) {
			BING_INFO_MAP.put(prizeGrade,new HashSet<String>());
		}
		String[] codes = thisCode.split(",");
		StringBuilder sb=new StringBuilder();
		for (String code : codes) {
			if (StringUtils.isNotEmpty(code)) {
				sb.append(CODE_INFO_MAP.get(code)).append(",");
			}
		}
		log.info("日志保存本次中奖：中奖级别:"+prizeGrade+";中奖工号："+thisCode+"中奖人员:"+sb.toString());
		Set<String> codeSet = BING_INFO_MAP.get(prizeGrade);
		Collections.addAll(codeSet,codes);
		return map;
	}

	/**
	 * 去抽奖页面
	 */
	@RequestMapping(value = "/goIndex")
	public String goIndex(HttpServletRequest request,Map<String,Object> map){
		String path =request.getSession().getServletContext().getRealPath("/");
		File dir=new File(path+"/upload");
		File[] files = dir.listFiles();
		if (files == null||files.length==0) {
			map.put("msg","您还没有导入Excel文件，请先导入!");
			return "error";
		}
		if(CODE_INFO_MAP.isEmpty()){
			getExcelUserToMap(files[0]);
		}
		String userInfo=getUserInfo();
		map.put("userInfo", userInfo);
		String allBingoCode = getAllBingoCode();
		map.put("alreadyBingo",allBingoCode);
		log.info("返回去抽奖的信息,alreadyBingo:"+allBingoCode);

		return "index";
	}

	/**
	 * 从缓存中得到用户信息
	 * @return
	 */
	private String getUserInfo(){
		StringBuilder sb=new StringBuilder();
		for (Map.Entry<String, String> entry : CODE_INFO_MAP.entrySet()) {
			//工号_部门-名字,工号_部门-名字,
			sb.append(entry.getKey()).append("_").append(entry.getValue()).append(",");
		}
		return sb.length()==0?sb.toString():sb.substring(0,sb.length()-1);
	}

	/**
	 * 得到所有中奖工号
	 * @return
	 */
	private String getAllBingoCode(){
		StringBuilder sb=new StringBuilder();
		for (Set<String> codes : BING_INFO_MAP.values()) {
			for (String code : codes) {
				//工号,工号,
				sb.append(code).append(",");
			}
		}
		return sb.length()==0?sb.toString():sb.substring(0,sb.length()-1);

	}
	/**
	 * 得到所有中奖工号,按奖品级别分组
	 * @return
	 */
	private String getBingoCodeByLevel(){
		StringBuilder sb=new StringBuilder();
		//"字符串格式奖品级别_工号1，工号2&奖品级别_工号1，工号2 ，例如1_1,2,3&2_4,5,6";
		for (Map.Entry<String, Set<String>> entry : BING_INFO_MAP.entrySet()) {
			String prizeLevel=entry.getKey();
			Set<String> codes=entry.getValue();
			sb.append(prizeLevel).append("_");

			for (String code : codes) {
				sb.append(code).append(",");
			}
			sb.append("&");
		}
		return sb.length()==0?sb.toString():sb.substring(0,sb.length()-1);

	}

	/**
	 * 把Excel表用户放入CODE_INFO_MAP
	 * @param file
	 */
	private void getExcelUserToMap(File file){
		FileInputStream fis =null;
		try {
			fis=new FileInputStream(file);

			Workbook workbook = null;
			try {
				workbook = new XSSFWorkbook(fis);
			} catch (Exception ex) {
				workbook = new HSSFWorkbook(fis);
			}

			Sheet sheet = workbook.getSheetAt(0);
			//行列都是从0开始的
			Row row=null;
			Cell cell = null;
			int totalRows=sheet.getPhysicalNumberOfRows();//总行数
			for (int i = 0; i < totalRows; i++) {
				if(i==0){
					continue;//忽略首行表头
				}
				row=sheet.getRow(i);
				cell=row.getCell(0);//第一列是工号

				double cellValue = cell.getNumericCellValue();
				String code=new DecimalFormat("#").format(cellValue);


				cell=row.getCell(1);//第二列是部门
				String dept_name=cell.getStringCellValue();
				cell=row.getCell(2);//第三列是姓名
				dept_name=dept_name+"-"+cell.getStringCellValue();
				CODE_INFO_MAP.put(code,dept_name);
			}

		}catch (Exception e){
			e.printStackTrace();
		}finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	/**
	 * 上传文件
	 * @param file
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/importFile",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> importFile(@RequestParam(value = "file", required = false) MultipartFile file,
							 HttpServletRequest request, ModelMap model) {
		Map<String,Object> result=new HashMap<String, Object>();
		result.put("success",true);
		System.out.println("开始");
		String path = request.getSession().getServletContext().getRealPath("upload");
		File dir=new File(path);
		if (dir.exists()) {
			for (File f : dir.listFiles()) {
				f.delete();
			}
		}
		String fileName = file.getOriginalFilename();
		System.out.println(path);
		File targetFile = new File(path, fileName);
		if(!targetFile.exists()){
			targetFile.mkdirs();
		}

		//保存
		try {
			file.transferTo(targetFile);
			BING_INFO_MAP.clear();
			CODE_INFO_MAP.clear();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("上传文件出错，",e);
			result.put("success",false);
		}
		model.addAttribute("fileUrl", request.getContextPath()+"/upload/"+fileName);
		log.info("上传文件成功");
		return result;
	}

	/**
	 * 下载文件
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/download")
	public String download(HttpServletRequest request,
						   HttpServletResponse response,Map<String,Object> map) throws Exception {
		String path =request.getSession().getServletContext().getRealPath("/");
		ServletOutputStream sos =null;
		FileInputStream fis =null;
		File file =null;

		try{
			File dir=new File(path+"/upload");
			if(!dir.exists()){
				map.put("msg","您还没有导入Excel文件，请先导入!");
				return "error";
			}
			File[] files = dir.listFiles();
			if (files == null||files.length==0) {
				map.put("msg","您还没有导入Excel文件，请先导入!");
				return "error";
			}
			File firstFile = files[0];
			String tempName =firstFile.getName();

			response.setContentType("application/vnd.ms.excel;charset=UTF-8");//设置本地文本
			response.setLocale(Locale.CHINESE);//设置本地化
			String agent =request.getHeader("User-Agent");//获取浏览器类型
			if(null !=agent && !"".equals(agent)){
				if(agent.indexOf("Firefox")>-1){//火狐浏览器处理方式
					agent=new String(tempName.getBytes("UTF-8"),"ISO8859-1");
				}else{//其他浏览器
					agent= URLEncoder.encode(tempName, "UTF-8");
				}
			}else{//未知
				agent=tempName;
			}
			String value =new StringBuilder("attachment;filename=").append(agent).toString();
			response.setHeader("Content-Disposition", value);//设置下载文件名

			file =firstFile;
			fis=new FileInputStream(file);
			sos=response.getOutputStream();
			int len =0;
			byte[] buffer =new byte[1024];
			while(-1!=(len=fis.read(buffer,0,buffer.length))){
				sos.write(buffer,0,len);
			}
			sos.flush();
			log.info("下载文件成功");
		}
		catch (Exception e) {
			e.printStackTrace();
			log.error("下载文件出错,",e);
			map.put("msg",e.getMessage());
		}finally{
			if(fis!=null){
				fis.close();
			}
			if(sos!=null){
				sos.close();
			}
		}
		return "error";
	}

}