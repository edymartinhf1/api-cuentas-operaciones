package com.bootcamp.bank;

import com.bootcamp.bank.operaciones.service.ComisionesService;
import com.bootcamp.bank.operaciones.service.OperacionCuentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ApiCuentasOperacionesApplicationTests {
	@Autowired
	private ComisionesService comisionesService;
	@Autowired
	private OperacionCuentaService operacionCuentaService;
	@Test
	void contextLoads() {
		assertThat(comisionesService).isNotNull();
		assertThat(operacionCuentaService).isNotNull();
	}

}
