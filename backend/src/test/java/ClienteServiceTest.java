import org.desafioestagio.backend.dto.ClienteDTO;
import org.desafioestagio.backend.model.Cliente;
import org.desafioestagio.backend.service.ClienteService;
import org.junit.Test;

import static net.bytebuddy.matcher.ElementMatchers.any;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void deveSalvarClienteValido() {
        ClienteDTO dto = new ClienteDTO(/* dados válidos */);
        when(clienteRepository.save(any())).thenReturn(new Cliente());

        ClienteDTO resultado = clienteService.salvar(dto);

        assertNotNull(resultado);
        verify(clienteRepository).save(any());
    }

    @Test
    void deveLancarExcecaoParaCpfDuplicado() {
        ClienteDTO dto = new ClienteDTO(/* com CPF existente */);
        when(clienteRepository.existsByCpfCnpj(dto.getCpfCnpj())).thenReturn(true);

        assertThrows(DuplicadoException.class, () -> clienteService.salvar(dto));
    }
}