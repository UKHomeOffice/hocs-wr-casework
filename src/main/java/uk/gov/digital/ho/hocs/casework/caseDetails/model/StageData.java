package uk.gov.digital.ho.hocs.casework.caseDetails.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stage_data")
@Getter
@NoArgsConstructor
public class StageData implements Serializable {

    @Setter
    @Column(name = "type")
    private String type;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name ="uuid")
    private UUID uuid = UUID.randomUUID();

    @Column(name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    @Setter
    @Column(name ="data")
    private String data;

    @Column(name = "case_uuid")
    private UUID caseUUID;

    public StageData(UUID caseUUID, String type, String data) {
        this.type = type;
        this.data = data;
        this.caseUUID = caseUUID;
    }
}
