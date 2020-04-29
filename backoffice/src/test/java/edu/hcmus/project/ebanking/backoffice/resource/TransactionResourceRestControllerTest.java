package edu.hcmus.project.ebanking.backoffice.resource;

import edu.hcmus.project.ebanking.backoffice.BackofficeApplication;
import edu.hcmus.project.ebanking.backoffice.BackofficeApplicationTests;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.TransactionResourceRestController;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.dto.TransactionQueryDto;
import edu.hcmus.project.ebanking.backoffice.service.TransactionService;
import edu.hcmus.project.ebanking.backoffice.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static edu.hcmus.project.ebanking.backoffice.utils.PageableAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = TransactionResourceRestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes={BackofficeApplicationTests.class})
public class TransactionResourceRestControllerTest {

    @MockBean
    private TransactionService tranferService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void evaluatesRetrievePageableTransactions() throws Exception {
        mockMvc.perform(get("/transactions")
                .param("startDate", "2020-02-26T15:14:03.000+0700")
                .param("endDate", "2020-02-26T15:14:03.000+0700")
                .param("page", "5")
                .param("size", "10")
                .param("sort", "date,desc"))
                .andExpect(status().isOk());

        ArgumentCaptor<TransactionQueryDto> queryCaptor =
                ArgumentCaptor.forClass(TransactionQueryDto.class);
        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);
        verify(tranferService).findAllTransaction(queryCaptor.capture(), pageableCaptor.capture());
        PageRequest pageable = (PageRequest) pageableCaptor.getValue();

        assertThat(pageable).hasPageNumber(5);
        assertThat(pageable).hasPageSize(10);
        assertThat(pageable).hasSort("date", Sort.Direction.DESC);
    }

}
