package dev.razorni.core.extras.report;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.razorni.core.Core;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReportHandler {

    private final Core plugin = Core.getInstance();

    private final MongoCollection<Document> reportCollection;

    @Getter
    private final List<Report> reports;

    public ReportHandler() {
        this.reports = new ArrayList<>();
        this.reportCollection = plugin.getMongoHandler().getMongoDatabase().getCollection("reports");
        loadReports();
    }

    public Report getReportByName(String search) {
        return this.reports.stream().filter(report -> report.getName().equalsIgnoreCase(search)).findFirst().orElse(null);
    }

    private void loadReports() {
        for (Document document : reportCollection.find()) {
            Report report = new Report(document.getString("name"), document.getString("message"), document.getString("sender"), document.getLong("createdAt"), document.getBoolean("report"));
            report.setServer(document.getString("server"));
            report.setResolved(document.getBoolean("resolved"));
            report.setResolvedAt(document.getLong("resolvedAt"));
            report.setResolvedBy(document.getString("resolvedBy"));
            report.setReported(document.getString("reported"));
            saveReport(report);
        }
    }

    public Optional<Document> getReportDocumentFromDb(String name){
        return Optional.ofNullable(reportCollection.find(Filters.eq("name", name)).first());
    }


    public void loadReportByName(String name) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            getReportDocumentFromDb(name).ifPresent(document -> {
                Report report = getReportByName(name);

                if (report != null) {
                    report.setName(document.getString("name"));
                    report.setMessage(document.getString("message"));
                    report.setReport(document.getBoolean("report"));
                    report.setCreatedAt(document.getLong("createdAt"));
                    report.setResolved(document.getBoolean("resolved"));
                    report.setResolvedAt(document.getLong("resolvedAt"));
                    report.setResolvedBy(document.getString("resolvedBy"));
                    report.setSender(document.getString("sender"));
                    report.setServer(document.getString("server"));
                    report.setReported(document.getString("reported"));
                } else {
                    report = new Report(document.getString("name"), document.getString("message"), document.getString("sender"), document.getLong("createdAt"), document.getBoolean("report"));
                    report.setResolved(document.getBoolean("resolved"));
                    report.setServer(document.getString("server"));
                    report.setResolvedAt(document.getLong("resolvedAt"));
                    report.setResolvedBy(document.getString("resolvedBy"));
                    report.setReported(document.getString("reported"));
                    reports.remove(report);
                    reports.add(report);
                }
            });
        });
    }

    public void saveReport(Report report) {
        if (!reports.contains(report)) {
            reports.add(report);
        }
        Document document = new Document();
        document.put("name", report.getName());
        document.put("sender", report.getSender());
        document.put("server", report.getServer());
        document.put("message", report.getMessage());
        document.put("createdAt", report.getCreatedAt());
        document.put("report", report.isReport());
        document.put("resolved", report.isResolved());
        document.put("resolvedBy", report.getResolvedBy());
        document.put("resolvedAt", report.getResolvedAt());
        document.put("reported", report.getReported());

        reportCollection.replaceOne(Filters.eq("name", report.getName()), document, new ReplaceOptions().upsert(true));
    }

    public void removeReport(Report report) {
        reports.remove(report);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            reportCollection.deleteOne(Filters.eq("name", report.getName())); // Deletes the report
        });
    }
}
