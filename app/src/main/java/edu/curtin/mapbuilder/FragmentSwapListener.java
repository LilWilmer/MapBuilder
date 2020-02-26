package edu.curtin.mapbuilder;

import androidx.fragment.app.Fragment;
public interface FragmentSwapListener
{
    void setMainFragment(Fragment frag, String tag);
    void setMainFragmentToRight(Fragment frag, String tag);
    void setMainFragmentDown(Fragment frag, String tag);
}
