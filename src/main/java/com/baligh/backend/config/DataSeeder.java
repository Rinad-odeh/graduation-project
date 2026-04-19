package com.baligh.backend.config;

import com.baligh.backend.model.Category;
import com.baligh.backend.model.Organization;
import com.baligh.backend.model.User;
import com.baligh.backend.model.enums.OrgStatus;
import com.baligh.backend.model.enums.UserRole;
import com.baligh.backend.repository.CategoryRepository;
import com.baligh.backend.repository.OrganizationRepository;
import com.baligh.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final OrganizationRepository orgRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        // Users
        User admin = userRepository.save(User.builder()
                .name("مدير النظام")
                .phone("0599999999")
                .role(UserRole.ADMIN)
                .build());

        User orgRep = userRepository.save(User.builder()
                .name("ممثل الجهة")
                .phone("0501234567")
                .role(UserRole.ORG_MEMBER)
                .build());

        User citizen = userRepository.save(User.builder()
                .name("أحمد محمد")
                .phone("0551234567")
                .role(UserRole.USER)
                .build());

        // Organization
        orgRepository.save(Organization.builder()
                .name("أمانة منطقة الرياض")
                .category("حكومي")
                .description("تتولى الأمانة تقديم الخدمات البلدية في منطقة الرياض")
                .contactEmail("info@amanah.gov.sa")
                .contactPhone("0112345678")
                .status(OrgStatus.APPROVED)
                .representative(orgRep)
                .build());

        orgRepository.save(Organization.builder()
                .name("شركة المياه الوطنية")
                .category("خدمات")
                .description("تقديم خدمات المياه والصرف الصحي")
                .contactEmail("contact@nwc.com.sa")
                .contactPhone("0114567890")
                .status(OrgStatus.PENDING)
                .representative(citizen) // placeholder
                .build());

        // Categories
        categoryRepository.saveAll(List.of(
                Category.builder().nameAr("طرق وإشارات").icon("car").color("#3B82F6").build(),
                Category.builder().nameAr("إنارة عامة").icon("flash").color("#F59E0B").build(),
                Category.builder().nameAr("نظافة ونفايات").icon("trash").color("#10B981").build(),
                Category.builder().nameAr("مياه وصرف صحي").icon("water").color("#06B6D4").build(),
                Category.builder().nameAr("حدائق ومرافق").icon("leaf").color("#22C55E").build(),
                Category.builder().nameAr("أضرار وتلف").icon("warning").color("#EF4444").build(),
                Category.builder().nameAr("ضوضاء ومخالفات").icon("volume-high").color("#8B5CF6").build(),
                Category.builder().nameAr("أخرى").icon("ellipsis-horizontal").color("#6B7280").build()
        ));

        System.out.println("=== Dev data seeded ===");
        System.out.println("Admin phone: 0599999999");
        System.out.println("Org rep phone: 0501234567");
        System.out.println("Citizen phone: 0551234567");
    }
}
