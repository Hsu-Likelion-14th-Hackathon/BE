package com.boardingpass.be.domain.floor;

import com.boardingpass.be.domain.floor.dto.FloorDetailResponse;
import com.boardingpass.be.domain.floor.dto.FloorListResponse;
import com.boardingpass.be.domain.floor.dto.FloorSummaryResponse;
import com.boardingpass.be.domain.store.Store;
import com.boardingpass.be.domain.store.StoreRepository;
import com.boardingpass.be.global.apiPayload.code.status.ErrorStatus;
import com.boardingpass.be.global.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FloorService {

  private final StoreRepository storeRepository;
  private final FloorRepository floorRepository;

  public FloorListResponse getFloors() {
    Store store = storeRepository.findFirstByOrderByIdAsc()
        .orElseThrow(() -> new GeneralException(ErrorStatus.STORE_NOT_FOUND));

    List<FloorSummaryResponse> floors = floorRepository
        .findByStoreIdOrderByFloorNoAsc(store.getId())
        .stream()
        .map(FloorSummaryResponse::from)
        .toList();

    return FloorListResponse.of(store.getName(), floors);
  }

  public FloorDetailResponse getFloorDetail(Long floorId) {
    Floor floor = floorRepository.findDetailById(floorId)
        .orElseThrow(() -> new GeneralException(ErrorStatus.FLOOR_NOT_FOUND));

    return FloorDetailResponse.from(floor);
  }
}