package android.project;

public class ContactInformation {

    private int _identifier;
    private String _name;
    private String _number;
    private String _image;
    private String _image2;
    private String _location;
    private String _date;


    public ContactInformation(){

    }

    public ContactInformation(String name, String number, String image, String image2, String geo, String date){
        this._name=name;
        this._number=number;
        this._image=image;
        this._image2 =image2;
        this._location=geo;
        this._date=date;
    }

    public int getIdentifier(){
        return _identifier;
    }
    public void setIdentifier(int id){
        this._identifier=id;
    }
    public String getName(){
        return this._name;
    }
    public void setName(String name){
        this._name=name;
    }
    public String getNumber(){
        return this._number;
    }
    public void setNumber(String number){
        this._number=number;
    }
    public String getImage(){
        return this._image;
    }
    public void setImage(String image){
        this._image=image;
    }
    public String getImage2(){return this._image2;}
    public void setImage2(String image2){this._image2=image2;}
    public String getLocation(){return this._location;}
    public void setLocation(String geo){this._location=geo;}
    public String getDate(){return this._date;}
    public void setDate(String date){this._date=date;}
}
