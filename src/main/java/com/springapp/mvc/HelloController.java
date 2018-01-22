package com.springapp.mvc;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
public class HelloController {

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
		String userInfo=getExcelUserInfo(files[0]);
		map.put("userInfo",userInfo);
		return "index";
	}
	private String getExcelUserInfo(File file){
		FileInputStream fis =null;
		StringBuffer sb=new StringBuffer();
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
				sb.append(new DecimalFormat("#").format(cellValue));

				cell=row.getCell(1);//第二列是名字
				sb.append("_").append(cell.getStringCellValue()).append(",");
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
		return sb.length()==0?sb.toString():sb.substring(0,sb.length()-1);
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
		String fileName = file.getOriginalFilename();
//        String fileName = new Date().getTime()+".jpg";
		System.out.println(path);
		File targetFile = new File(path, fileName);
		if(!targetFile.exists()){
			targetFile.mkdirs();
		}

		//保存
		try {
			file.transferTo(targetFile);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success",false);
		}
		model.addAttribute("fileUrl", request.getContextPath()+"/upload/"+fileName);

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
		}
		catch (Exception e) {
			e.printStackTrace();
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