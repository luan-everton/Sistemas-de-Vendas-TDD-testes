package com.gft.tdd.service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.gft.tdd.email.NotificadorEmail;
import com.gft.tdd.model.Pedido;
import com.gft.tdd.model.StatusPedido;
import com.gft.tdd.model.builder.PedidoBuilder;
import com.gft.tdd.repository.Pedidos;
import com.gft.tdd.sms.NotificadorSMS;

public class PedidoServiceTest {
	
	private PedidoService pedidoService;
	 
	@Mock
	private Pedidos pedidos;
	
	@Mock
	private NotificadorEmail notificadorEmail;
	
	@Mock
	private NotificadorSMS notificadorSMS;
	
	private Pedido pedido;
	
	@Before
	public void setup() { 
		MockitoAnnotations.initMocks(this); 
		List<AcaoLancamentoPedido>acoes = Arrays.asList(pedidos,notificadorEmail, notificadorSMS);
		pedidoService = new PedidoService(pedidos, acoes);
		pedido = new PedidoBuilder().comValor(100.0).para("Joao", "joao@.com", "99969699").construir();
		
	}
	
	
	
	@Test
	public void deveCalcularImposto() throws Exception {
		double imposto = pedidoService.lancar(pedido);
		assertEquals(10.0, imposto, 0.0001);

	}
  
	@Test
	public void deveSalvarNoBancoDeDados() throws Exception {
		pedidoService.lancar(pedido);
		Mockito.verify(pedidos).executar(pedido);
	}
	@Test
	public void deveNotificarPorEmail() throws Exception {
	 pedidoService.lancar(pedido);
	 Mockito.verify(notificadorEmail).executar(pedido);
		
		
	}
	@Test
	public void deveNotificarPorSms() throws Exception {
		pedidoService.lancar(pedido);
		Mockito.verify(notificadorSMS).executar(pedido);
		
	}
	
	@Test
	public void devePagarPendentePendente() throws Exception {
		Long codigoPedido = 135L;
		Pedido pedidoPendente =  new Pedido();
		pedidoPendente.setStatus(StatusPedido.PENDENTE);
		Mockito.when(pedidos.buscarPeloCodigo(codigoPedido)).thenReturn(pedidoPendente);
		
		Pedido pedidopago = pedidoService.pagar(codigoPedido);
		
		assertEquals(StatusPedido.PAGO, pedidopago.getStatus());
		
	}

}







