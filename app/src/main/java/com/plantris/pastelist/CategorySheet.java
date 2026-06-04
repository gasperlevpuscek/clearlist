package com.plantris.pastelist;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CategorySheet {

    public interface OnCategorySelectedListener {
        void onCategorySelected(@Nullable String category);
    }

    private CategorySheet() {
    }

    public static void show(
            @NonNull AppCompatActivity activity,
            @Nullable String currentCategory,
            @NonNull OnCategorySelectedListener listener
    ) {
        BottomSheetDialog dialog = new BottomSheetDialog(activity);
        ViewGroup root = activity.findViewById(android.R.id.content);
        View view = activity.getLayoutInflater().inflate(R.layout.item_category, root, false);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);

        RadioGroup categoryRadioGroup = view.findViewById(R.id.categoryRadioGroup);
        Button btnSaveCategory = view.findViewById(R.id.btnSaveCategory);

        // Set current category
        if (currentCategory != null && !currentCategory.isEmpty()) {
            switch (currentCategory) {
                case "School":
                    categoryRadioGroup.check(R.id.category_school);
                    break;
                case "Work":
                    categoryRadioGroup.check(R.id.category_work);
                    break;
                case "Personal":
                    categoryRadioGroup.check(R.id.category_home);
                    break;
                case "None":
                    categoryRadioGroup.check(R.id.category_personal);
                    break;
            }
        }

        btnSaveCategory.setOnClickListener(v -> {
            int selectedId = categoryRadioGroup.getCheckedRadioButtonId();
            String selectedCategory = null;

            if (selectedId == R.id.category_school) {
                selectedCategory = "School";
            } else if (selectedId == R.id.category_work) {
                selectedCategory = "Work";
            } else if (selectedId == R.id.category_home) {
                selectedCategory = "Personal";
            } else if (selectedId == R.id.category_personal) {
                selectedCategory = "None";
            }

            listener.onCategorySelected(selectedCategory);
            dialog.dismiss();
        });

        dialog.show();
    }
}

