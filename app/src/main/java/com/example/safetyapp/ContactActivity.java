
package com.example.safetyapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safetyapp.ContactManager;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private List<SosContact> contactList;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        contactList = ContactManager.loadContacts(this);

        if (contactList == null) {
            contactList = new ArrayList<>();
        }

        RecyclerView recyclerView =
                findViewById(R.id.rv_contacts);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));

        adapter = new ContactAdapter(
                contactList,
                this::onDeleteContact);

        recyclerView.setAdapter(adapter);

        Button btnAdd =
                findViewById(R.id.btn_add_contact);

        btnAdd.setOnClickListener(v ->
                showAddContactDialog());
    }

    private void showAddContactDialog() {

        View dialogView = LayoutInflater.from(this)
                .inflate(
                        R.layout.dialog_add_contact,
                        null
                );

        EditText etName =
                dialogView.findViewById(
                        R.id.et_contact_name);

        EditText etPhone =
                dialogView.findViewById(
                        R.id.et_contact_phone);

        new AlertDialog.Builder(this)
                .setTitle("Add Emergency Contact")
                .setView(dialogView)

                .setPositiveButton("Save",
                        (dialog, which) -> {

                            String name =
                                    etName.getText()
                                            .toString()
                                            .trim();

                            String phone =
                                    etPhone.getText()
                                            .toString()
                                            .trim();

                            if (TextUtils.isEmpty(name)
                                    || TextUtils.isEmpty(phone)) {

                                Toast.makeText(
                                        this,
                                        "Fields cannot be empty",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            SosContact contact =
                                    new SosContact(name, phone);

                            ContactManager.addContact(
                                    this,
                                    contact
                            );

                            contactList.add(contact);

                            adapter.notifyItemInserted(
                                    contactList.size() - 1
                            );

                            Toast.makeText(
                                    this,
                                    "Contact Added", Toast.LENGTH_SHORT).show();
                        })

                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    private void onDeleteContact(int index) {

        if (index < 0
                || index >= contactList.size()) {
            return;
        }

        ContactManager.removeContact(this, index);

        contactList.remove(index);

        adapter.notifyItemRemoved(index);

        Toast.makeText(this, "Contact Removed", Toast.LENGTH_SHORT).show();
    }

    // ============================
    // RecyclerView Adapter
    // ============================

    static class ContactAdapter extends
            RecyclerView.Adapter<ContactAdapter.ViewHolder> {

        interface OnDeleteListener {
            void onDelete(int index);
        }

        private final List<SosContact> contacts;
        private final OnDeleteListener listener;

        ContactAdapter(
                List<SosContact> contacts,
                OnDeleteListener listener
        ) {
            this.contacts = contacts;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent,
                int viewType
        ) {

            View view = LayoutInflater.from(
                            parent.getContext())
                    .inflate(
                            R.layout.item_contact_activity,
                            parent,
                            false
                    );

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                @NonNull ViewHolder holder,
                int position
        ) {

            SosContact contact =
                    contacts.get(position);

            holder.tvName.setText(
                    contact.getName());

            holder.tvPhone.setText(
                    contact.getPhone());

            if(contact.isPrimary()){
                holder.btnPrimary.setText("Primary");
            }else{
                holder.btnPrimary.setText("Make Primary");
            }

            holder.btnPrimary.setOnClickListener(v -> {

                ContactManager.setPrimaryContact(
                        v.getContext(),
                        holder.getAdapterPosition()
                );

                for(int i=0;i<contacts.size();i++){
                    contacts.get(i).setPrimary(false);
                }

                contacts.get(holder.getAdapterPosition())
                        .setPrimary(true);

                notifyDataSetChanged();
            });

            holder.btnDelete.setOnClickListener(v -> {

                int pos = holder.getAdapterPosition();

                if (pos != RecyclerView.NO_POSITION) {
                    listener.onDelete(pos);
                }
            });
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        static class ViewHolder
                extends RecyclerView.ViewHolder {

            TextView tvName;
            TextView tvPhone;
            Button btnDelete;
            Button btnPrimary;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(
                        R.id.tv_contact_name);

                tvPhone = itemView.findViewById(
                        R.id.tv_contact_phone);

                btnDelete = itemView.findViewById(
                        R.id.btn_delete_contact);

                btnPrimary = itemView.findViewById(
                        R.id.btn_primary_contact);
            }
        }
    }
}
