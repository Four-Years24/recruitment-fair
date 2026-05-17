package com.recruitment.service.impl;

import com.recruitment.common.BusinessException;
import com.recruitment.common.PageResult;
import com.recruitment.dto.AuditDTO;
import com.recruitment.dto.RegistrationPageDTO;
import com.recruitment.entity.Company;
import com.recruitment.entity.JobFair;
import com.recruitment.entity.Position;
import com.recruitment.entity.Registration;
import com.recruitment.mapper.CompanyMapper;
import com.recruitment.mapper.JobFairMapper;
import com.recruitment.mapper.PositionMapper;
import com.recruitment.mapper.RegistrationMapper;
import com.recruitment.service.RegistrationService;
import com.recruitment.vo.RegistrationDetailVO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Resource
    private RegistrationMapper registrationMapper;

    @Resource
    private PositionMapper positionMapper;

    @Resource
    private CompanyMapper companyMapper;

    @Resource
    private JobFairMapper jobFairMapper;

    @Override
    public PageResult<RegistrationDetailVO> page(RegistrationPageDTO dto) {
        long total = registrationMapper.count(dto);
        List<RegistrationDetailVO> list = registrationMapper.page(dto);
        return new PageResult<>(total, dto.getPageNum(), dto.getPageSize(), list);
    }

    @Override
    public RegistrationDetailVO getDetail(Long id) {
        // 1. 查报名记录
        Registration reg = registrationMapper.findById(id);
        if (reg == null) {
            throw new BusinessException("报名记录不存在");
        }

        // 2. 查关联的企业和招聘会
        Company company = companyMapper.findById(reg.getCompanyId());
        JobFair jobFair = jobFairMapper.findById(reg.getJobFairId());

        // 3. 组装 VO
        RegistrationDetailVO detail = new RegistrationDetailVO();
        detail.setId(reg.getId());
        detail.setCompanyId(reg.getCompanyId());
        detail.setCompanyName(company != null ? company.getName() : null);
        detail.setIndustry(company != null ? company.getIndustry() : null);
        detail.setContactName(company != null ? company.getContactName() : null);
        detail.setContactPhone(company != null ? company.getContactPhone() : null);
        detail.setContactEmail(company != null ? company.getContactEmail() : null);
        detail.setJobFairId(reg.getJobFairId());
        detail.setJobFairTitle(jobFair != null ? jobFair.getTitle() : null);
        detail.setBoothNumber(reg.getBoothNumber());
        detail.setStatus(reg.getStatus());
        detail.setRejectReason(reg.getRejectReason());
        detail.setCreateTime(reg.getCreateTime());

        // 4. 查岗位列表
        List<Position> positions = positionMapper.findByRegistrationId(id);
        List<RegistrationDetailVO.PositionVO> pvos = new ArrayList<>();
        for (Position p : positions) {
            RegistrationDetailVO.PositionVO pvo = new RegistrationDetailVO.PositionVO();
            pvo.setTitle(p.getTitle());
            pvo.setHeadcount(p.getHeadcount());
            pvo.setEducation(p.getEducation());
            pvo.setMajorRequire(p.getMajorRequire());
            pvo.setSalaryRange(p.getSalaryRange());
            pvo.setWorkCity(p.getWorkCity());
            pvos.add(pvo);
        }
        detail.setPositions(pvos);

        return detail;
    }

    @Override
    public void audit(Long id, AuditDTO dto) {
        Registration reg = registrationMapper.findById(id);
        if (reg == null) {
            throw new BusinessException("报名记录不存在");
        }
        if (dto.getStatus() == 2 && (dto.getRejectReason() == null || dto.getRejectReason().trim().isEmpty())) {
            throw new BusinessException("驳回时必须填写原因");
        }
        registrationMapper.updateStatus(id, dto.getStatus(), dto.getRejectReason());
    }

    @Override
    public List<RegistrationDetailVO> getByJobFairId(Long jobFairId) {
        List<RegistrationDetailVO> list = registrationMapper.findByJobFairId(jobFairId);
        for (RegistrationDetailVO vo : list) {
            List<Position> positions = positionMapper.findByRegistrationId(vo.getId());
            List<RegistrationDetailVO.PositionVO> pvos = new ArrayList<>();
            for (Position p : positions) {
                RegistrationDetailVO.PositionVO pvo = new RegistrationDetailVO.PositionVO();
                pvo.setTitle(p.getTitle());
                pvo.setHeadcount(p.getHeadcount());
                pvo.setEducation(p.getEducation());
                pvo.setMajorRequire(p.getMajorRequire());
                pvo.setSalaryRange(p.getSalaryRange());
                pvo.setWorkCity(p.getWorkCity());
                pvos.add(pvo);
            }
            vo.setPositions(pvos);
        }
        return list;
    }

    @Override
    public void export(RegistrationPageDTO dto, HttpServletResponse response) {
        // 不分页，导出所有符合条件的记录
        dto.setPageNum(1);
        dto.setPageSize(10000);
        List<RegistrationDetailVO> list = registrationMapper.page(dto);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("报名数据");

            // 表头
            Row header = sheet.createRow(0);
            String[] headers = {"企业名称", "行业", "联系人", "电话", "邮箱",
                    "招聘会", "展位号", "状态", "提交时间"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            // 数据行
            for (int i = 0; i < list.size(); i++) {
                RegistrationDetailVO vo = list.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(vo.getCompanyName() != null ? vo.getCompanyName() : "");
                row.createCell(1).setCellValue(vo.getIndustry() != null ? vo.getIndustry() : "");
                row.createCell(2).setCellValue(vo.getContactName() != null ? vo.getContactName() : "");
                row.createCell(3).setCellValue(vo.getContactPhone() != null ? vo.getContactPhone() : "");
                row.createCell(4).setCellValue(vo.getContactEmail() != null ? vo.getContactEmail() : "");
                row.createCell(5).setCellValue(vo.getJobFairTitle() != null ? vo.getJobFairTitle() : "");
                row.createCell(6).setCellValue(vo.getBoothNumber() != null ? vo.getBoothNumber() : "");
                row.createCell(7).setCellValue(statusText(vo.getStatus()));
                row.createCell(8).setCellValue(vo.getCreateTime() != null ? vo.getCreateTime().toString() : "");
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = URLEncoder.encode("报名数据.xlsx", StandardCharsets.UTF_8.name());
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.flush();
        } catch (Exception e) {
            throw new BusinessException("导出失败：" + e.getMessage());
        }
    }

    private String statusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待审核";
            case 1: return "已通过";
            case 2: return "已驳回";
            default: return "未知";
        }
    }
}
