package com.example.inventoryservice;

import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class InventoryServiceApplication {
	private final InventoryRepository inventoryRepository;

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData() {
		return args -> {
			Inventory iPhone13SpaceGrey = new Inventory();
			iPhone13SpaceGrey.setSkuCode("iphone_13_space_grey");
			iPhone13SpaceGrey.setQuantity(10);

			Inventory iPhone13Green = new Inventory();
			iPhone13Green.setSkuCode("iphone_13_green");
			iPhone13Green.setQuantity(0);

			inventoryRepository.save(iPhone13SpaceGrey);
			inventoryRepository.save(iPhone13Green);
		};
	}
}
