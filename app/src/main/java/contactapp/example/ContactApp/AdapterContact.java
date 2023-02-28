package contactapp.example.ContactApp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterContact extends RecyclerView.Adapter<AdapterContact.ContactViewHolder>{

    private Context context;
    private ArrayList<ModelContact> contactList;
    private DbHelper dbHelper;


    public AdapterContact(Context context, ArrayList<ModelContact> contactList) {
        this.context = context;
        this.contactList = contactList;
        dbHelper = new DbHelper(context);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_contact_item,parent,false);
        ContactViewHolder vh = new ContactViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {

        ModelContact modelContact = contactList.get(position);

        //get data

        String id  = modelContact.getId();
        String image = modelContact.getImage();
        String name = modelContact.getName();
        String phone = modelContact.getPhone();
        String email = modelContact.getEmail();
        String note = modelContact.getNote();
        String addedTime = modelContact.getAddedTime();
        String updateTime = modelContact.getUpdatedTime();

        //set data in view

        holder.contactName.setText(name);
        if (image.equals("null")){
            holder.contactImage.setImageResource(R.drawable.ic_baseline_person_24);
        }else {
            holder.contactImage.setImageURI(Uri.parse(image));
        }

        //handle click listener
        holder.contactDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //handle item klik dan tampilkan detail kontak
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // membuat intent untuk pindah ke contactsDetails Activity dengan contact id

                Intent intent = new Intent(context,ContactDetail.class);
                intent.putExtra("contactId",id);
                context.startActivity(intent); // now get data from details Activity
            }
        });

        // handle editBtn click
        holder.contactEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //buat intent untuk memindahkan AddEditActivity untuk memperbarui data
                Intent intent = new Intent(context,AddEditContact.class);

                intent.putExtra("ID",id);
                intent.putExtra("NAME",name);
                intent.putExtra("PHONE",phone);
                intent.putExtra("EMAIL",email);
                intent.putExtra("NOTE",note);
                intent.putExtra("ADDED_TIME",addedTime);
                intent.putExtra("UPDATE_TIME",updateTime);
                intent.putExtra("IMAGE",image);

                // meneruskan data boolean untuk mendefinisikannya untuk tujuan edit
                intent.putExtra("isEditMode",true);

                //star intent
                context.startActivity(intent);

            }
        });

        // handle delete click
        holder.contactDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // membutuhkan database helper
                dbHelper.deleteContact(id);

                //refresh data by calling resume state of MainActivity
                ((MainActivity)context).onResume();

            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder{

        // view untuk row_contact_item
        ImageView contactImage,contactDial;
        TextView contactName,contactEdit,contactDelete;
        RelativeLayout relativeLayout;


        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            //init view
            contactImage = itemView.findViewById(R.id.contact_image);
            contactDial = itemView.findViewById(R.id.contact_number_dial);
            contactName = itemView.findViewById(R.id.contact_name);
            contactDelete = itemView.findViewById(R.id.contact_delete);
            contactEdit = itemView.findViewById(R.id.contact_edit);
            relativeLayout = itemView.findViewById(R.id.mainLayout);

        }
    }
}
