package com.example.demo.controller.Points;

import com.example.demo.service.PointService;
import com.example.demo.service.dto.LeaderboardDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointsController.class)
public class PointsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @Test
    void testGetUserPoints() throws Exception {
        String userId = "user1";
        when(pointService.getUserPoints(userId)).thenReturn(100);

        mockMvc.perform(get("/points/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value(0))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.totalPoints").value(100));
    }

    @Test
    void testAddPoints() throws Exception {
        String json = "{\"userId\":\"user1\", \"amount\":100, \"reason\":\"test\"}";
        when(pointService.addPoints("user1", 100, "test")).thenReturn(100);

        mockMvc.perform(post("/points")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value(0))
                .andExpect(jsonPath("$.data.totalPoints").value(100));
    }

    @Test
    void testGetLeaderboard() throws Exception {
        when(pointService.getLeaderboard(10)).thenReturn(List.of(
                new LeaderboardDto("user1", 100L),
                new LeaderboardDto("user2", 50L)
        ));

        mockMvc.perform(get("/points/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value(0))
                .andExpect(jsonPath("$.data[0].userId").value("user1"))
                .andExpect(jsonPath("$.data[0].total").value(100));
    }
}
