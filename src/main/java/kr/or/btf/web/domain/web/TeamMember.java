package kr.or.btf.web.domain.web;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_teammember")
@DynamicUpdate
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teammber_pid")
    private Long id;

    @Column(name = "contest_pid")
    private Long contset_pid;

    @Column(name = "app_pid")
    private Long app_pid;

    @Column(name = "nm")
    private String nm;

    @Column(name = "moblphon")
    private String moblphon;

    @Column(name = "affi")
    private String affi;

    @Column(name = "brthyday")
    private String brthyday;
}
