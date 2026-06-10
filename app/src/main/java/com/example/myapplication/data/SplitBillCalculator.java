package com.example.myapplication.data;

import com.example.myapplication.model.PersonSummary;
import com.example.myapplication.model.ReceiptItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SplitBillCalculator {

    // Sekarang mengembalikan List<PersonSummary> bukan String
    public static List<PersonSummary> calculateSplitBill(List<ReceiptItem> receiptItemList, List<String> choosenList,
                                                         long globalTax, long globalService,
                                                         long globalDiscount, long globalPackaging, long globalDelivery) {
        List<PersonSummary> hasilAkhir = new ArrayList<>();

        if (receiptItemList.isEmpty()) {
            return hasilAkhir;
        }

        HashMap<String, Double> subTotals = new HashMap<>();
        for (String person : choosenList) {
            subTotals.put(person, 0.0);
        }

        double totalBelanjaanBarang = 0.0;

        for (ReceiptItem item : receiptItemList) {
            List<String> shared = item.getSharedWith();
            if (shared.isEmpty()) {
                shared.add("Pribadi");
            }

            double splitPrice = (double) item.getPrice() / shared.size();
            for (String person : shared) {
                double currentTotal = subTotals.getOrDefault(person, 0.0);
                subTotals.put(person, currentTotal + splitPrice);
            }
            totalBelanjaanBarang += item.getPrice();
        }

        double totalExtraFees = globalTax + globalService + globalPackaging + globalDelivery - globalDiscount;

        for (String person : subTotals.keySet()) {
            double personSubTotal = subTotals.get(person);

            if (personSubTotal > 0) {
                double personExtraFee = 0.0;
                if (totalBelanjaanBarang > 0) {
                    personExtraFee = (personSubTotal / totalBelanjaanBarang) * totalExtraFees;
                }
                double personGrandTotal = personSubTotal + personExtraFee;

                // Masukkan ke dalam objek, bukan string gabungan
                hasilAkhir.add(new PersonSummary(person, personSubTotal, personExtraFee, personGrandTotal));
            }
        }

        return hasilAkhir;
    }
}