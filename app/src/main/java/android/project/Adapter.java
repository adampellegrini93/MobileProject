package android.project;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Adapter extends BaseAdapter {

    private List<ContactInformation> items;
    private Context context;
    private LayoutInflater inflater;


    public Adapter(Context c, List<ContactInformation> _items){
        inflater = LayoutInflater.from(c);
        this.items=_items;
        this.context=c;
    }

    @Override
    public int getCount(){
        return items.size();
    }

    @Override
    public Object getItem(int position){
        return position;
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ContactInformation contact = items.get(position);
        View view = convertView;

        if(view == null)
            view = inflater.inflate(R.layout.activity_contact_list, null);

        TextView name = (TextView)view.findViewById(R.id.txt_name);
        TextView number = (TextView)view.findViewById(R.id.txt_number);
        ImageView image = (ImageView)view.findViewById(R.id.contactlist_image);

        name.setText(contact.getName());
        number.setText(contact.getNumber());
        image.setImageBitmap(BitmapFactory.decodeFile(contact.getImage()));
        image.setImageBitmap(BitmapFactory.decodeFile(contact.getImage2()));

        return view;
    }

}
