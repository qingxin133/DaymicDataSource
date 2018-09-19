package cn.com.hellowood.dynamicdatasource.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.com.hellowood.dynamicdatasource.configuration.DynamicDataSourceContextHolder;
import cn.com.hellowood.dynamicdatasource.configuration.SpringUtil;
/**
 * Multiple DataSource Aspect
 *
 * @author HelloWood
 * @date 2017-08-15 11:37
 * @email hellowoodes@gmail.com
 */
@Aspect
@Component
public class DynamicDataSourceAspect {
	
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    private static boolean flag = true;
    private final String[] QUERY_PREFIX = {"All"};
    private static String daopackage = "cn.com.hellowood.dynamicdatasource.mapper.";
//    private final  String pointStr = "execution( * cn.com.hellowood.dynamicdatasource.service.*.*(..))";
    private final  String pointStr = "execution( * cn.com.hellowood.dynamicdatasource.mapper.*.*(..))";
    
    /**
     * Dao aspect.
     */
    @Pointcut(pointStr)
    public void daoAspect() {
    }

    /**
     * Switch DataSource
     *
     * @param point the point
     */
    @Before("daoAspect()")
    public void switchDataSource(JoinPoint point) {
//        Boolean isQueryMethod = isQueryMethod(point.getSignature().getName());
//        if (isQueryMethod) {
//            DynamicDataSourceContextHolder.useSlaveDataSource();
//            DynamicDataSourceContextHolder.useMasterDataSource();
            logger.debug("Switch DataSource to [{}] in Method [{}]",DynamicDataSourceContextHolder.getDataSourceKey(), point.getSignature());
//        }
    }


    /**
     * Restore DataSource
     *
     * @param point the point
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws ClassNotFoundException 
     */
    @After("daoAspect())")
    public void restoreDataSource(JoinPoint point) throws IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException{


        logger.info("Restore DataSource to [{}] in Method [{}]",DynamicDataSourceContextHolder.getDataSourceKey(), point.getSignature());
    }

  
    //在方法执行完结后打印返回内容 
    @AfterReturning(returning = "result",pointcut = "daoAspect()") 
    public void methodAfterReturing(JoinPoint point,Object result) throws ClassNotFoundException, NoSuchMethodException, SecurityException{
		logger.info("--------------返回内容1----------------"); 
		logger.info("result1:"+result.toString()); 
		
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        logger.info("URL : " + request.getRequestURL().toString());
        logger.info("HTTP_METHOD : " + request.getMethod());
        logger.info("IP : " + request.getRemoteAddr());
        logger.info("CLASS_METHOD : " + point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName());
        logger.info("ARGS : " + Arrays.toString(point.getArgs()));
		
	    Boolean isQueryMethod = isQueryMethod(point.getSignature().getName());
        if (isQueryMethod) {
			MethodSignature signature = (MethodSignature)point.getSignature();
			Method method = signature.getMethod(); //获取被拦截的方法 
			Type type1 = method.getGenericReturnType();
			Class<?> daoClass = method.getDeclaringClass();
			Object daoBean = SpringUtil.getBean(daoClass);
			
//			Class<?> returnCType = method.getReturnType();
		    if(type1.getTypeName().indexOf("List")!=-1) {
				List list1 = (List)result;
				
				for(int i=0;i<DynamicDataSourceContextHolder.slaveDataSourceKeys.size();i++) {
					//切换数据库
			        DynamicDataSourceContextHolder.useSlaveDataSource();
	//		        Method methodDao = daoClass.getMethod(method.getName(), method.getParameterTypes());
			        Object obj2 = ReflectionUtils.invokeMethod(method,daoBean,point.getArgs());
			        List list2 = (List)obj2;
					logger.info("--------------返回内容"+i+"----------------");
					logger.info("result2:"+list2.toString()); 
					list1.addAll(list2);
				}
				System.out.println("result:"+list1.toString());
				
		    }
	        DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    /**
     * Judge if method start with query prefix
     *
     * @param methodName
     * @return
     */
    private Boolean isQueryMethod(String methodName) {
        for (String prefix : QUERY_PREFIX) {
            if (methodName.indexOf(prefix)!=-1) {
                return true;
            }
        }
        return false;
    }

}
