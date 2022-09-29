#include <gtkmm.h>

class Window : public Gtk::Window {
public:
  Gtk::VBox vbox;
  Gtk::Entry entry;
  Gtk::Entry entry2;
  Gtk::Button button;
  Gtk::Label label;
  Gtk::Label label1;
  Gtk::Label label2;

  Window() {
    button.set_label("Combine names");

    vbox.pack_start(label1);  //Add the widget label to vbox
    vbox.pack_start(entry);  //Add the widget entry to vbox
    vbox.pack_start(label2);  //Add the widget label to vbox
    vbox.pack_start(entry2);  //Add the widget entry to vbox
    vbox.pack_start(button); //Add the widget button to vbox
    vbox.pack_start(label);  //Add the widget label to vbox
    add(vbox);  //Add vbox to window
    show_all(); //Show all widgets
    entry.signal_changed().connect([this]() {
        if(entry.get_text().empty() || entry2.get_text().empty() )button.set_sensitive(false);
        else button.set_sensitive(true);
    });
    label1.set_text("First name");
    label2.set_text("Last name");

    entry2.signal_changed().connect([this]() {
        if(entry2.get_text().empty() || entry.get_text().empty() )button.set_sensitive(false);
        else button.set_sensitive(true);
    });
    
    button.set_sensitive(false);

    button.signal_clicked().connect([this]() {
      label.set_text(entry.get_text() + " " + entry2.get_text());
    });
  }
};

int main() {
  Gtk::Main gtk_main;
  Window window;
  gtk_main.run(window);
}