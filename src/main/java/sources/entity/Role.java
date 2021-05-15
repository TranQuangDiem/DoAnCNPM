package sources.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String ten;

    public Role(long id, String ten) {
        this.id = id;
        this.ten = ten;
    }

    public Role() {
    }
}
