package gov.nasa.arc.planworks.viz.viewMgr.contentSpecWindow;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MergeBox extends JPanel {
  private JCheckBox merge;
  public MergeBox() {
    GridBagLayout gridBag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    setLayout(gridBag);

    merge = new JCheckBox("Merge tokens", false);
    c.weightx = 1;
    c.gridx = 0;
    c.gridy = 0;
    gridBag.setConstraints(merge, c);
    add(merge);
  }
  public boolean getValue() {
    return merge.isSelected();
  }
  public void reset() {
    merge.setSelected(false);
  }
}
