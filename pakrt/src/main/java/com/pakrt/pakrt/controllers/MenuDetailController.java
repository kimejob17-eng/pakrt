package com.pakrt.pakrt.controllers;

import com.pakrt.pakrt.models.menu.MenuDetail;
import com.pakrt.pakrt.request.menu.MenuDetailRequest;
import com.pakrt.pakrt.responses.BasicResponse;
import com.pakrt.pakrt.responses.ResponseBuilder;
import com.pakrt.pakrt.responses.ResultResponse;
import com.pakrt.pakrt.services.MenuDetailService;
import com.pakrt.pakrt.utils.DataMap;
import com.pakrt.pakrt.utils.MessageDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/menu-detail")
public class MenuDetailController {

    @Autowired
    private MenuDetailService menuDetailService;

    /**
     * POST /menu-detail/create
     * Sesuai standar Nexus Multilateral 2.3.26
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody MenuDetailRequest request) {
        try {
            MenuDetail created = menuDetailService.create(request);
            return ResponseBuilder.buildSuccessResponse(created);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400204", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * PUT /menu-detail/update
     * Sesuai standar Nexus Multilateral 2.3.26
     */
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody MenuDetailRequest request) {
        try {
            MenuDetail updated = menuDetailService.update(request);
            return ResponseBuilder.buildSuccessResponse(updated);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400204", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * GET /menu-detail/list
     * Query: pn (page number), ps (page size), n (name filter), menuId
     * Sesuai standar Nexus Multilateral 2.3.26
     */
    @GetMapping("/list")
    public ResponseEntity<?> list(
            @RequestParam(required = false) Integer pn,
            @RequestParam(required = false) Integer ps,
            @RequestParam(required = false) Integer pageNo,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false, name = "n") String name,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String menuId,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyEnabled) {
        try {
            String searchKeyword = (name != null && !name.trim().isEmpty()) ? name : keyword;
            List<MenuDetail> allList = menuDetailService.getAll(menuId, searchKeyword, onlyEnabled);
            int total = (allList != null) ? allList.size() : 0;

            Integer pageIndex = (pn != null) ? pn : pageNo;
            Integer limit = (ps != null) ? ps : pageSize;

            List<MenuDetail> pagedList = allList;
            if (pageIndex != null && limit != null && limit > 0 && allList != null) {
                int fromIndex = Math.min(pageIndex * limit, total);
                int toIndex = Math.min(fromIndex + limit, total);
                pagedList = allList.subList(fromIndex, toIndex);
            }

            return ResponseEntity.ok(ResultResponse.Get("200100", DataMap.Mapped(pagedList, total)));
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * GET /menu-detail/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            MenuDetail detail = menuDetailService.getById(id);
            return ResponseBuilder.buildSuccessResponse(detail);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * POST /menu-detail/sequence
     * Mendapatkan nomor sequence berikutnya untuk menu terkait.
     * Request Body: { "menuId": "..." }
     * Sesuai standar Nexus Multilateral 2.3.26
     */
    @PostMapping("/sequence")
    public ResponseEntity<?> getNextSequence(@RequestBody MenuDetailRequest request) {
        try {
            Integer nextSequence = menuDetailService.getNextSequence(request.getMenuId());
            Map<String, Object> data = new HashMap<>();
            data.put("menuId", request.getMenuId());
            data.put("nextSequence", nextSequence);
            return ResponseBuilder.buildSuccessResponse(data);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400202", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * POST /menu-detail/sequence/list
     * Mendapatkan daftar sequence yang sudah ada.
     * Request Body: { "menuId": "..." }
     * Sesuai standar Nexus Multilateral 2.3.26
     */
    @PostMapping("/sequence/list")
    public ResponseEntity<?> getSequenceList(@RequestBody MenuDetailRequest request) {
        try {
            List<Integer> sequences = menuDetailService.getSequenceList(request.getMenuId());
            return ResponseEntity.ok(ResultResponse.Get("200100", sequences));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400202", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * POST /menu-detail/delete
     * Sesuai standar Nexus Multilateral 2.3.26
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestBody MenuDetailRequest request) {
        try {
            menuDetailService.delete(request.getId());
            return ResponseEntity.ok(BasicResponse.Get("200100", "Menu Detail berhasil dihapus"));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * DELETE /menu-detail/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            menuDetailService.delete(id);
            return ResponseEntity.ok(BasicResponse.Get("200100", "Menu Detail berhasil dihapus"));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }
}