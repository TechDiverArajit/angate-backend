package io.angate.AnGate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Category;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event extends BaseEntity {

    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private String venue;
    @NotNull
    private LocalDateTime startTime;

    private Boolean active = true;


    private String imageUrl;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.UPCOMING;

    @JsonIgnore
    @OneToMany(mappedBy = "event" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<TicketType> ticketTypes = new ArrayList<>();


    public Status fetchStatus(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = this.startTime.plusHours(5);
        if(now.isBefore(this.startTime)){
            return Status.UPCOMING;
        } else if (now.isAfter(endTime)) {
            return Status.COMPLETED;
        }
        return Status.LIVE;
    }


    public enum Status{
        UPCOMING,
        LIVE,
        COMPLETED,
        CANCELLED
    }


}

