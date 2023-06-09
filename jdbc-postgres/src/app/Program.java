package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import db.DB;
import entities.Order;
import entities.OrderStatus;
import entities.Product;

public class Program {

	public static void main(String[] args) throws SQLException {
		
		Connection conn = DB.getConnection();
	
		Statement statement = conn.createStatement();
			
//		ResultSet resultSetProduct = statement.executeQuery("select * from tb_product");
//
//		while (resultSetProduct.next()) {
//			Product product = instantiateProduct(resultSetProduct);
//			System.out.println(product);
//		}
//
//		System.out.println("----------");
//
//
//		ResultSet resultSetOrder = statement.executeQuery("select * from tb_order");
//
//		while (resultSetOrder.next()) {
//			Order order = instantiateOrder(resultSetOrder);
//			System.out.println(order);
//		}

		System.out.println("----------");


		ResultSet resultSetOrder_Product = statement.executeQuery("SELECT * FROM tb_order " +
				"INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id " +
				"INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id");

		Map<Long, Order> map = new HashMap<>();
		Map<Long, Product> prods = new HashMap<>();

		while (resultSetOrder_Product.next()) {

			Long orderId = resultSetOrder_Product.getLong("order_id");
			if (map.get(orderId) == null) {
				Order order_product = instantiateOrder(resultSetOrder_Product);
				map.put(orderId, order_product);
			}

			Long productId = resultSetOrder_Product.getLong("product_id");
			if (prods.get(productId) == null) {
				Product p = instantiateProduct(resultSetOrder_Product);
				prods.put(productId, p);
			}

			map.get(orderId).getProducts().add(prods.get(productId));
		}

		for (Long orderId : map.keySet()){
			System.out.println(map.get(orderId));
			for (Product p : map.get(orderId).getProducts()){
				System.out.println(p);
			}
			System.out.println("");
		}

	}


	private static Product instantiateProduct(ResultSet rs) throws SQLException {
		Product product = new Product();
		product.setId(rs.getLong("product_id"));
		product.setDescription(rs.getString("description"));
		product.setImageUri(rs.getString("image_uri"));
		product.setName(rs.getString("name"));
		product.setPrice(rs.getDouble("price"));
		return product;
	}

	private static Order instantiateOrder(ResultSet rs) throws SQLException {
		Order order = new Order();
		order.setId(rs.getLong("order_id"));
		order.setLatitude(rs.getDouble("latitude"));
		order.setLongitude(rs.getDouble("longitude"));
		order.setMoment(rs.getTimestamp("moment").toInstant());
		order.setStatus(OrderStatus.values()[rs.getInt("status")]);
		return order;
	}
}
