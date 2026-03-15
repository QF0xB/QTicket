package de.qf0xb.qticket.auth.repository.account;

import de.qf0xb.qticket.auth.model.account.jpa.AuthAccountEntity;
import de.qf0xb.qticket.auth.service.SearchRequest;
import org.springframework.data.domain.Page;

public interface AuthAccountEntityRepositoryExtension {
    Page<AuthAccountEntity> search(SearchRequest searchRequest);
}
