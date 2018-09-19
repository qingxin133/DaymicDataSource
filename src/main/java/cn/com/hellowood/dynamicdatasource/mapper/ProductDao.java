package cn.com.hellowood.dynamicdatasource.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import cn.com.hellowood.dynamicdatasource.modal.Product;

/**
 * Product mapper for operate data of products table
 *
 * @author HelloWood
 * @date 2017-07-11 10:54
 * @Email hellowoodes@gmail.com
 */

@Mapper
public interface ProductDao {
	
	List<Map> equipList();
	
    Product select(@Param("id") long id);

    Integer update(Product product);

    Integer insert(Product product);

    Integer delete(long productId);

    List<Product> getAllProduct();
}
