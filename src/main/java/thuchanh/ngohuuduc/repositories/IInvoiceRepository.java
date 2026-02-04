package thuchanh.ngohuuduc.repositories;

import thuchanh.ngohuuduc.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInvoiceRepository extends JpaRepository<Invoice,Long> {
    
}
