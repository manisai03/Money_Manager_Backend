package com.example.money_mhttps.start.spring.io.anager.service;



import com.example.money_mhttps.start.spring.io.anager.dto.ExpenseDTO;
import com.example.money_mhttps.start.spring.io.anager.dto.IncomeDTO;
import com.example.money_mhttps.start.spring.io.anager.entity.CategoryEntity;
import com.example.money_mhttps.start.spring.io.anager.entity.ExpenseEntity;
import com.example.money_mhttps.start.spring.io.anager.entity.IncomeEntity;
import com.example.money_mhttps.start.spring.io.anager.entity.ProfileEntity;
import com.example.money_mhttps.start.spring.io.anager.repositoty.CategoryRepository;
import com.example.money_mhttps.start.spring.io.anager.repositoty.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    public IncomeDTO addIncome(IncomeDTO dto){
        ProfileEntity profile= profileService.getCurrentProfile();
        CategoryEntity category=categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        IncomeEntity newIncome=toEntity(dto,profile,category);
        newIncome=incomeRepository.save(newIncome);
        return toDTO(newIncome);
    }

//    //Retrieves all expenses for current month/based on start date and end date
//    public List<IncomeDTO> getCurrentMonthIncomeForCurrentUser(){
//        ProfileEntity profile=profileService.getCurrentProfile();
//        LocalDate now=LocalDate.now();
//        LocalDate startDate=now.withDayOfMonth(1);
//        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
//        List<IncomeEntity> list=incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
//        return list.stream().map(this::toDTO).toList();
//    }

    public void deleteIncome(Long incomeId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity entity = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Income not found"));

        if (!entity.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized to delete this income");
        }

        incomeRepository.delete(entity);
    }


    //helper methods
    private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .category(category)
                .profile(profile)
                .build();
    }

    public List<IncomeDTO> getLatest5IncomesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<IncomeEntity> list=incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    //filter expenses
    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<IncomeEntity> list=incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalIncomeForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        BigDecimal total=incomeRepository.findTotalIncomeByProfileId(profile.getId());
        return total!=null ? total: BigDecimal.ZERO;
    }


    private IncomeDTO toDTO(IncomeEntity entity){
        return IncomeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory()!=null ? entity.getCategory().getId(): null)
                .categoryName(entity.getCategory()!=null ? entity.getCategory().getName(): "N/A")
                .amount(entity.getAmount() != null ? entity.getAmount() : BigDecimal.ZERO)
                .date(entity.getDate() != null ? entity.getDate() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        ProfileEntity profile=profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list=incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    public ByteArrayInputStream exportIncomesToExcel() {
        List<IncomeDTO> incomes = getCurrentMonthIncomesForCurrentUser();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Incomes");

            // Header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Category");
            headerRow.createCell(3).setCellValue("Amount");
            headerRow.createCell(4).setCellValue("Date");

            // Data rows
            int rowIdx = 1;
            for (IncomeDTO income : incomes) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(income.getId());
                row.createCell(1).setCellValue(income.getName());
                row.createCell(2).setCellValue(income.getCategoryName());
                row.createCell(3).setCellValue(income.getAmount().doubleValue());
                row.createCell(4).setCellValue(income.getDate().toString());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export Excel file", e);
        }
    }
}