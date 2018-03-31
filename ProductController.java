package com.arch.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.arch.constants.Constants;
import com.arch.model.AllCategory;
import com.arch.model.Category;
import com.arch.model.Commercial;
import com.arch.model.Entrepot;
import com.arch.model.HotProduct;
import com.arch.model.Product;
import com.arch.model.Resource;
import com.arch.service.EntrepotService;
import com.arch.service.ProductService;
import com.arch.service.ResourceService;
import com.arch.tools.Tools;


@Controller
@RequestMapping("/product")
public class ProductController{

	@Autowired
	private ProductService productService;
	@Autowired
	private EntrepotService entrepotService;
	@Autowired
	private ResourceService resourceService;
	
	@RequestMapping("/save")
	public String save(HttpServletRequest request,HttpServletResponse response){
		JSONObject json = new JSONObject();
		Product product = new Product();
//		String createUser = request.getParameter("createUser") != null ? request.getParameter("createUser") : ((Commercial) request.getSession().getAttribute("commercial")).getCommercialName();
		try {
			 
			product.setAdvertise(request.getParameter("advertise"));
			product.setBradeId(Long.parseLong(request.getParameter("bradeId")));
			product.setCategoryId(request.getParameter("categoryId"));
			product.setCostPrice(request.getParameter("costPrice"));//成本价
			product.setDescription(request.getParameter("description"));
			product.setEntrepotId(Long.parseLong(request.getParameter("entrepotId")));
			product.setIsPopular(request.getParameter("isPopular"));//是否推广
			product.setStatus("入库");
			product.setType(request.getParameter("type"));
			product.setKeyWord(request.getParameter("keyWord"));//关键词
			product.setPrice(request.getParameter("price"));//售价
			product.setPriceSection(request.getParameter("priceSection"));//价格区间
			product.setProduceAddr(request.getParameter("produceAddr"));//产地
			product.setProductName(request.getParameter("productName"));
			product.setRepelnDesc(request.getParameter("repelnDesc"));//补充说明
			product.setRepertoryCount(Long.parseLong(request.getParameter("repertoryCount")));//库存
			product.setSelfSupport(request.getParameter("selfSupport"));//是否自营
			product.setSupply(request.getParameter("supply"));//厂商
			product.setCreateTime(Tools.formatDate(new Date()));
			product.setCreateUser("");
			product.setRemark(request.getParameter("remark"));
			product.setBrandName(request.getParameter("brandName"));
			Long returnCode = productService.save(product);
			//这样就得到了自增的id
			if(null != returnCode){//返回保存的id值
				 long  startTime=System.currentTimeMillis();
		         //将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
		        CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver(
		                request.getSession().getServletContext());
		        //检查form中是否有enctype="multipart/form-data"
		        if(multipartResolver.isMultipart(request))
		        {
		        	List<Resource>  resourceList = new ArrayList<Resource>();
		            //将request变成多部分request
		            MultipartHttpServletRequest multiRequest=(MultipartHttpServletRequest)request;
		           //获取multiRequest 中所有的文件名
		            Iterator iter=multiRequest.getFileNames();
		             
		            String realPath = request.getSession().getServletContext().getRealPath("resouce");
		            int i = 0;
		            while(iter.hasNext())
		            {
		            	 i++;
		                //一次遍历所有文件
		                MultipartFile file=multiRequest.getFile(iter.next().toString());
		                if(file!=null)
		                {
		                	String uuid = UUID.randomUUID().toString();
		                	String [] fileNameArr = file.getOriginalFilename().split("\\.");
		                	String fileName = uuid + "." + fileNameArr[1];
//		                    String path=realPath+"/"+file.getOriginalFilename();
		                    String path=realPath+"/"+fileName;
		                    //上传
		                    file.transferTo(new File(path));
		                    
		                    Resource resource = new Resource();
		                    resource.setUrl("resouce/"+fileName);
		                    resource.setProductId(product.getProductId());
		                    resource.setSort(i+"");
		                    resource.setCreateUser("");
		                    resource.setCreateTime(Tools.formatDate(new Date()));
		                    resourceList.add(resource);
		                }
		               
		                 
		            }
		            
		            //上传完成，将资源数据保存至数据库
		            //TODO 后期优化为批量操作
		            
//		            resourceService.batchSave(resourceList);
		            if(null!=resourceList && resourceList.size() > 0){
		            	for(Resource resource:resourceList){
		            		resourceService.save(resource);
		            	}
		            }
		           
		        }
		        long  endTime=System.currentTimeMillis();
		      //  System.out.println("方法三的运行时间："+String.valueOf(endTime-startTime)+"ms");
			}
			json.put("returnCode", Constants.SUCCESS_CODE);	
			json.put("returnMsg", Constants.SUCCESS_MSG);
			json.put("addCode", returnCode);
//			Tools.returnAjaxDataOut(json,response);
		} catch (Exception e) {
			e.printStackTrace();
			json.put("returnCode", Constants.FAILUER_CODE);	
			json.put("returnMsg", e.getLocalizedMessage());		
			try {
				Tools.returnAjaxDataOut(json,response);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		request.setAttribute("entrepotId", request.getParameter("entrepotId"));
		return "/admin/productList";
	}
	
	@RequestMapping("/likeProducts")
	public void likeProducts(HttpServletRequest request,HttpServletResponse response){
		JSONObject json = new JSONObject();
		Product product = new Product();
		try {
			product.setAdvertise(request.getParameter("advertise"));
			Long bradeId = null;
			if(request.getParameter("bradeId") != null){
				bradeId = Long.parseLong(request.getParameter("bradeId"));
			}
			Long productId = null;
			if(request.getParameter("productId") != null){
				productId = Long.parseLong(request.getParameter("productId"));
			}
			product.setBradeId(bradeId);
			product.setBrandName(request.getParameter("brandName"));
			product.setIsNew(request.getParameter("isNew"));
			product.setIsUse(request.getParameter("isUse"));
			product.setProductId(productId);
			product.setType(request.getParameter("type"));
			product.setCategoryId(request.getParameter("categoryId"));
			product.setCostPrice(request.getParameter("costPrice"));//成本价
			product.setDescription(request.getParameter("description"));
			Long entrepotId = null;
			if(request.getParameter("entrepotId") != null){
				entrepotId = Long.parseLong(request.getParameter("entrepotId"));
			}
			product.setEntrepotId(entrepotId);
			product.setIsPopular(request.getParameter("isPopular"));//是否推广
			product.setKeyWord(request.getParameter("keyWord"));//关键词
			product.setPrice(request.getParameter("price"));//售价
			product.setPriceSection(request.getParameter("priceSection"));//价格区间
			product.setProduceAddr(request.getParameter("produceAddr"));//产地
			product.setProductName(request.getParameter("productName"));
			product.setRepelnDesc(request.getParameter("repelnDesc"));//补充说明
			Long repertoryCount = null;
			if(request.getParameter("repertoryCount") != null){
				repertoryCount = Long.parseLong(request.getParameter("repertoryCount"));
			}
			product.setRepertoryCount(repertoryCount);//库存
			product.setSelfSupport(request.getParameter("selfSupport"));//是否自营
			product.setStatus(request.getParameter("status"));
			product.setSupply(request.getParameter("supply"));//厂商
			product.setCreateTime(request.getParameter("createTime"));
			product.setCreateUser(request.getParameter("createUser"));
			product.setUpdateTime(request.getParameter("updateTime"));
			product.setUpdateUser(request.getParameter("updateUser"));
			product.setRemark(request.getParameter("remark"));
			product.setUserLocation(request.getParameter("userLocation"));
			List<Product> likeProducts = productService.likeProducts(product);
			for (int i = 0; i < likeProducts.size(); i++) {
				Resource re = new Resource();
				re.setProductId(likeProducts.get(i).getProductId());
				List<Resource> resourceList = resourceService.findByProAndType(re);
				likeProducts.get(i).setResourceList(resourceList);
				resourceList=null;
			}
			json.put("returnCode", Constants.SUCCESS_CODE);	
			json.put("returnMsg", Constants.SUCCESS_MSG);
			json.put("likeProducts", likeProducts);
			Tools.returnAjaxDataOut(json,response);
		} catch (Exception e) {
			e.printStackTrace();
			json.put("returnCode", Constants.FAILUER_CODE);	
			json.put("returnMsg", e.getLocalizedMessage());		
			try {
				Tools.returnAjaxDataOut(json,response);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	@RequestMapping("/isHot")
	public void isHot(HttpServletRequest request,HttpServletResponse response){
		JSONObject json = new JSONObject();
		Product product = new Product();
		try {
			product.setIsHot("1");
			List<HotProduct> likeProducts = productService.isHot(product);
			/*for (int i = 0; i < likeProducts.size(); i++) {
				Resource re = new Resource();
				re.setProductId(likeProducts.get(i).getProductId());
				List<Resource> resourceList = resourceService.findByProAndType(re);
				likeProducts.get(i).setResourceList(resourceList);
				resourceList=null;
			}*/
			json.put("returnCode", Constants.SUCCESS_CODE);	
			json.put("returnMsg", Constants.SUCCESS_MSG);
			json.put("likeProducts", likeProducts);
			Tools.returnAjaxDataOut(json,response);
		} catch (Exception e) {
			e.printStackTrace();
			json.put("returnCode", Constants.FAILUER_CODE);	
			json.put("returnMsg", e.getLocalizedMessage());		
			try {
				Tools.returnAjaxDataOut(json,response);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	@RequestMapping("/saveApp")
	public void saveApp(HttpServletRequest request,HttpServletResponse response){
		JSONObject json = new JSONObject();
		Product product = new Product();
		String createUser = request.getParameter("createUser") != null ? request.getParameter("createUser") : ((Commercial) request.getSession().getAttribute("commercial")).getCommercialName();
		try {
			 
			product.setAdvertise(request.getParameter("advertise"));
			product.setBradeId(Long.parseLong(request.getParameter("bradeId")));
			product.setBrandName(request.getParameter("brandName"));
			product.setCategoryId(request.getParameter("categoryId"));
			product.setCostPrice(request.getParameter("costPrice"));//成本价
			product.setDescription(request.getParameter("description"));
			product.setEntrepotId(Long.parseLong(request.getParameter("entrepotId")));
			product.setIsPopular(request.getParameter("isPopular"));//是否推广
			product.setIsNew(request.getParameter("isNew"));
			product.setStatus("入库");
			product.setType(request.getParameter("type"));
			product.setKeyWord(request.getParameter("keyWord"));//关键词
			product.setPrice(request.getParameter("price"));//售价
			product.setPriceSection(request.getParameter("priceSection"));//价格区间
			product.setProduceAddr(request.getParameter("produceAddr"));//产地
			product.setProductName(request.getParameter("productName"));
			product.setRepelnDesc(request.getParameter("repelnDesc"));//补充说明
			product.setRepertoryCount(Long.parseLong(request.getParameter("repertoryCount")));//库存
			product.setSelfSupport(request.getParameter("selfSupport"));//是否自营
			product.setStatus(request.getParameter("status"));
			product.setSupply(request.getParameter("supply"));//厂商
			product.setCreateTime(Tools.formatDate(new Date()));
			product.setCreateUser(createUser);
			product.setRemark(request.getParameter("remark"));
			Long returnCode = productService.save(product);
			//这样就得到了自增的id
			if(null != returnCode){//返回保存的id值
				 long  startTime=System.currentTimeMillis();
		         //将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
		        CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver(
		                request.getSession().getServletContext());
		        //检查form中是否有enctype="multipart/form-data"
		        if(multipartResolver.isMultipart(request))
		        {
		        	List<Resource>  resourceList = new ArrayList<Resource>();
		            //将request变成多部分request
		            MultipartHttpServletRequest multiRequest=(MultipartHttpServletRequest)request;
		           //获取multiRequest 中所有的文件名
		            Iterator iter=multiRequest.getFileNames();
		             
		            String realPath = request.getSession().getServletContext().getRealPath("resouce");
		            int i = 0;
		            while(iter.hasNext())
		            {
		            	 i++;
		                //一次遍历所有文件
		                MultipartFile file=multiRequest.getFile(iter.next().toString());
		                if(file!=null)
		                {
		                	String uuid = UUID.randomUUID().toString();
		                	String [] fileNameArr = file.getOriginalFilename().split("\\.");
		                	String fileName = uuid + "." + fileNameArr[1];
//		                    String path=realPath+"/"+file.getOriginalFilename();
		                    String path=realPath+"/"+fileName;
		                    //上传
		                    file.transferTo(new File(path));
		                    
		                    Resource resource = new Resource();
		                    resource.setUrl("resouce/"+fileName);
		                    resource.setProductId(product.getProductId());
		                    resource.setSort(i+"");
		                    resource.setCreateUser(createUser);
		                    resource.setCreateTime(Tools.formatDate(new Date()));
		                    resourceList.add(resource);
		                }
		               
		                 
		            }
		            
		            //上传完成，将资源数据保存至数据库
		            //TODO 后期优化为批量操作
		            
//		            resourceService.batchSave(resourceList);
		            if(null!=resourceList && resourceList.size() > 0){
		            	for(Resource resource:resourceList){
		            		resourceService.save(resource);
		            	}
		            }
		           
		        }
		        long  endTime=System.currentTimeMillis();
		      //  System.out.println("方法三的运行时间："+String.valueOf(endTime-startTime)+"ms");
			}
			json.put("returnCode", Constants.SUCCESS_CODE);	
			json.put("returnMsg", Constants.SUCCESS_MSG);
			json.put("addCode", returnCode);
			Tools.returnAjaxDataOut(json,response);
		} catch (Exception e) {
			e.printStackTrace();
			json.put("returnCode", Constants.FAILUER_CODE);	
			json.put("returnMsg", e.getLocalizedMessage());		
//			try {
//				Tools.returnAjaxDataOut(json,response);
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
		}
		request.setAttribute("entrepotId", request.getParameter("entrepotId"));
	}
	@RequestMapping("/selectByCity")
	public void selectByCity(HttpServletRequest request,HttpServletResponse response){
		JSONObject json = new JSONObject();
		try {
			Map map = new HashMap();
			map.put("type", request.getParameter("type"));//加盟商
			map.put("city", request.getParameter("city"));
			
			List<Product> products = productService.selectByCity(map);
			json.put("returnCode", Constants.SUCCESS_CODE);	
			json.put("returnMsg", Constants.SUCCESS_MSG);
			json.put("products", products);
			json.put("isStop", 1);
			Tools.returnAjaxDataOut(json,response);
		} catch (Exception e) {
			e.printStackTrace();
			json.put("returnCode", Constants.FAILUER_CODE);	
			json.put("returnMsg", e.getLocalizedMessage());		
			try {
				Tools.returnAjaxDataOut(json,response);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	@RequestMapping("/selectByGroupType")
	public void selectByGroupType(HttpServletRequest request,HttpServletResponse response){
		JSONObject json = new JSONObject();
		try {
			Map map = new HashMap();
			map.put("type", request.getParameter("type"));//加盟商
			List<Category> categories = productService.selectByGroupType(map);
			json.put("returnCode", Constants.SUCCESS_CODE);	
			json.put("returnMsg", Constants.SUCCESS_MSG);
			json.put("categories", categories);
			json.put("isStop", 1);
			Tools.returnAjaxDataOut(json,response);
		} catch (Exception e) {
			e.printStackTrace();
			json.put("returnCode", Constants.FAILUER_CODE);	
			json.put("returnMsg", e.getLocalizedMessage());		
			try {
				Tools.returnAjaxDataOut(json,response);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	@RequestMapping("/selectCategory")
	public void selectCategory(HttpServletRequest request,HttpServletResponse response){
		JSONObject json = new JSONObject();
		try {
			Map map = new HashMap();
			map.put("type", request.getParameter("type"));//加盟商
			List<AllCategory> allCategories = productService.selectCategory(map);
			json.put("returnCode", Constants.SUCCESS_CODE);	
			json.put("returnMsg", Constants.SUCCESS_MSG);
			json.put("categories", allCategories);
			Tools.returnAjaxDataOut(json,response);
		} catch (Exception e) {
			e.printStackTrace();
			json.put("returnCode", Constants.FAILUER_CODE);	
			json.put("returnMsg", e.getLocalizedMessage());		
			try {
				Tools.returnAjaxDataOut(json,response);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	
	@RequestMapping("/update")
	public void update(HttpServletRequest request,HttpServletResponse response){
		JSONObject json = new JSONObject();
		Product product = new Product();
		try {
			product.setAdvertise(request.getParameter("advertise"));
			Long bradeId = null;
			if(request.getParameter("bradeId") != null){
				bradeId = Long.parseLong(request.getParameter("bradeId"));
			}
			product.setBradeId(bradeId);
			product.setBrandName(request.getParameter("brandName"));
			product.setType(request.getParameter("type"));
			product.setIsNew(request.getParameter("isNew"));
			product.setCategoryId(request.getParameter("categoryId"));
			product.setCostPrice(request.getParameter("costPrice"));//成本价
			product.setDescription(request.getParameter("description"));
			Long entrepotId = null;
			if(request.getParameter("entrepotId") != null){
				entrepotId = Long.parseLong(request.getParameter("entrepotId"));
			}
			product.setEntrepotId(entrepotId);
			product.setProductId(Long.parseLong(request.getParameter("productId")));
			product.setIsPopular(request.getParameter("isPopular"));//是否推广
			product.setKeyWord(request.getParameter("keyWord"));//关键词
			product.setPrice(request.getParameter("price"));//售价
			product.setPriceSection(request.getParameter("priceSection"));//价格区间
			product.setProduceAddr(request.getParameter("produceAddr"));//产地
			product.setProductName(request.getParameter("productName"));
			product.setRepelnDesc(request.getParameter("repelnDesc"));//补充说明
			Long repertoryCount = null;
			if(request.getParameter("repertoryCount") != null){
				repertoryCount = Long.parseLong(request.getParameter("repertoryCount"));
			}
			product.setRepertoryCount(repertoryCount);//库存
			product.setSelfSupport(request.getParameter("selfSupport"));//是否自营
			product.setStatus(request.getParameter("status"));
			product.setSupply(request.getParameter("supply"));//厂商
			product.setUpdateTime(Tools.formatDate(new Date()));
			product.setUpdateUser(request.getParameter("loginName"));
			product.setRemark(request.getParameter("remark"));
			product.setIsUse(request.getParameter("isUse"));
			product.setIsHot(request.getParameter("isHot"));
			
			int updCode = productService.update(product);
			json.put("returnCode", Constants.SUCCESS_CODE);	
			json.put("returnMsg", Constants.SUCCESS_MSG);
			json.put("updCode", updCode);
			Tools.returnAjaxDataOut(json,response);
		} catch (Exception e) {
			e.printStackTrace();
			json.put("returnCode", Constants.FAILUER_CODE);	
			json.put("returnMsg", e.getLocalizedMessage());		
			try {
				Tools.returnAjaxDataOut(json,response);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/*@RequestMapping("/delete")
	public void delete(HttpServletRequest request,HttpServletResponse response){
		JSONObject json = new JSONObject();
		
		try {
			Long productId = Long.parseLong(request.getParameter("productId"));
			int delCode = productService.delete(productId);
			json.put("returnCode", Constants.SUCCESS_CODE);	
			json.put("returnMsg", Constants.SUCCESS_MSG);
			json.put("delCode", delCode);
			Tools.returnAjaxDataOut(json,response);
		} catch (Exception e) {
			e.printStackTrace();
			json.put("returnCode", Constants.FAILUER_CODE);	
			json.put("returnMsg", e.getLocalizedMessage());		
			try {
				Tools.returnAjaxDataOut(json,response);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}*/
	
	@RequestMapping("/likePageProducts")
	public void likePageProducts(HttpServletRequest request,HttpServletResponse response){
		JSONObject json = new JSONObject();
		Product product = new Product();
		int currentNum = request.getParameter("currentNum") == null ? 1 : Integer.parseInt(request.getParameter("currentNum"));
		int pageSize = Constants.PAGE_SIZE;
		int firstResult = (currentNum-1)*pageSize;
		/*firstResult = (currentNum-1)*pageSize;
		if(1 != currentNum){
			firstResult = (currentNum-1)*pageSize + 1;
		}else{
			firstResult = (currentNum-1)*pageSize;
		}*/
		try {
			product.setFirstResult(firstResult);
			product.setPageSize(pageSize);
			product.setAdvertise(request.getParameter("advertise"));
			Long bradeId = null;
			if(request.getParameter("bradeId") != null){
				bradeId = Long.parseLong(request.getParameter("bradeId"));
			}
			product.setBradeId(bradeId);
			product.setBrandName(request.getParameter("brandName"));
			product.setType(request.getParameter("type"));
			product.setCategoryId(request.getParameter("categoryId"));
			product.setCostPrice(request.getParameter("costPrice"));//成本价
			product.setDescription(request.getParameter("description"));
			Long entrepotId = null;
			if(request.getParameter("entrepotId") != null){
				entrepotId = Long.parseLong(request.getParameter("entrepotId"));
			}
			product.setEntrepotId(entrepotId);
			product.setIsPopular(request.getParameter("isPopular"));//是否推广
			product.setKeyWord(request.getParameter("keyWord"));//关键词
			product.setPrice(request.getParameter("price"));//售价
			product.setPriceSection(request.getParameter("priceSection"));//价格区间
			product.setProduceAddr(request.getParameter("produceAddr"));//产地
			product.setProductName(request.getParameter("productName"));
			product.setRepelnDesc(request.getParameter("repelnDesc"));//补充说明
			Long repertoryCount = null;
			if(request.getParameter("repertoryCount") != null){
				repertoryCount = Long.parseLong(request.getParameter("repertoryCount"));
			}
			product.setRepertoryCount(repertoryCount);//库存
			product.setSelfSupport(request.getParameter("selfSupport"));//是否自营
			product.setStatus(request.getParameter("status"));
			product.setSupply(request.getParameter("supply"));//厂商
			product.setCreateTime(request.getParameter("createTime"));
			product.setCreateUser(request.getParameter("createUser"));
			product.setUpdateTime(request.getParameter("updateTime"));
			product.setUpdateUser(request.getParameter("updateUser"));
			product.setRemark(request.getParameter("remark"));
			
			List<Product> likeProducts = productService.likePageProducts(product);
			//总共的行数  
			int levelCount = productService.getCount(product);
			//总共的页数  
		    int pageCount=(int)Math.ceil(levelCount*1.0/pageSize); 
			json.put("returnCode", Constants.SUCCESS_CODE);	
			json.put("returnMsg", Constants.SUCCESS_MSG);
			json.put("currentNum", currentNum);
			json.put("pageCount", pageCount);
			json.put("likeProducts", likeProducts);
			Tools.returnAjaxDataOut(json,response);
		} catch (Exception e) {
			e.printStackTrace();
			json.put("returnCode", Constants.FAILUER_CODE);	
			json.put("returnMsg", e.getLocalizedMessage());		
			try {
				Tools.returnAjaxDataOut(json,response);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	@RequestMapping("/findById")
	public void findById(HttpServletRequest request,HttpServletResponse response){
		JSONObject json = new JSONObject();
		try {
			
			Product product = productService.findById(Long.parseLong(request.getParameter("productId")));
			Resource resource = new Resource();
			resource.setProductId(Long.parseLong(request.getParameter("productId")));
			List<Resource> resourceList = resourceService.findByProAndType(resource);
			product.setResourceList(resourceList);
			json.put("returnCode", Constants.SUCCESS_CODE);	
			json.put("returnMsg", Constants.SUCCESS_MSG);
			json.put("product", product);
			Tools.returnAjaxDataOut(json,response);
		} catch (Exception e) {
			e.printStackTrace();
			json.put("returnCode", Constants.FAILUER_CODE);	
			json.put("returnMsg", e.getLocalizedMessage());		
			try {
				Tools.returnAjaxDataOut(json,response);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	
	@RequestMapping("/productList")
	public String productList(HttpServletRequest request){
		request.setAttribute("entrepotId", request.getParameter("entrepotId"));
		return "/admin/productList";
	}
	@RequestMapping("/saveUI")
	public String saveUI(HttpServletRequest request){
		Long entrepotId = Long.parseLong(request.getParameter("entrepotId"));
		Entrepot entrepot = entrepotService.findById(entrepotId);
		request.setAttribute("entrepot", entrepot);
		return "/admin/product/save";
	}
	@RequestMapping("/detailUI")
	public String detailUI(HttpServletRequest request){
		Long productId = Long.parseLong(request.getParameter("productId"));
		request.setAttribute("productId", productId);
		return "/admin/product/detail";
	}
	
	@RequestMapping("/findDetByH5Id")
	public String findDetByH5Id(HttpServletRequest request){
		Long productId = Long.parseLong(request.getParameter("productId"));
		request.setAttribute("productId", productId);
		return "/proDetH5";
	}
	
}
