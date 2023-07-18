package com.bootcamp.bank;

import com.bootcamp.bank.operaciones.model.dao.OperacionCtaDao;
import com.bootcamp.bank.operaciones.service.ComisionesService;
import com.bootcamp.bank.operaciones.service.OperacionCuentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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

	@Test
	void findAllAccount() {
		// When
		List<OperacionCtaDao> operaciones = this.operacionCuentaService.findAll().collectList().block();
		// Then
		assertThat(operaciones.size()>0);
	}

}
