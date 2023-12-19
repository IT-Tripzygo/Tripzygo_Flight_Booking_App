package in.tripzygo.tripzygoflightbookingapp.Modals;

public class Modelclass {

    private String Image,code;

    private String Name, price;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    private boolean check;

    public Modelclass(String image, String Name, String price, boolean check) {
        this.Image = image;
        this.Name = Name;
        this.price = price;
        this.check = check;
    }


    public Modelclass(String name) {
        Name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
