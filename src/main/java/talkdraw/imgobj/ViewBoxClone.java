package talkdraw.imgobj;

import talkdraw.imgobj.base.ViewBox;

public class ViewBoxClone {
    /** 物件的 ID */
    protected final int ID;
    /** 物件名稱 </p>*/
    protected final String name;
    /** <p>不透明度</p>*/
    protected final int alpha;
    /** {@code 水平} 與 {@code 垂直} 翻轉 */
    protected final boolean filpH, filpV;
    

    /** 拷貝建構子 
     *  <p>已經將以下數值儲存：</p>
     *  <p>{@code ID}、{@code 名稱}、{@code 透明度}、{@code 翻轉}</p>
     *  @param viewbox 欲儲存的 {@link ViewBox} */
    public ViewBoxClone( ViewBox viewbox ){
        this.ID         = viewbox.getID();
        this.name       = viewbox.getName();
        this.alpha      = viewbox.getAlpha();
        this.filpH      = viewbox.getFilp()[0];
        this.filpV      = viewbox.getFilp()[1];
    }

    /** 取得 ID  @return 編號 ID {@code [int]}*/
    public final int getID(){ return ID; }

    /** 取得 Name  @return 名稱 ID {@code [String]}*/
    public final String getName(){ return name; }

    /** 取得 Opacity 其值{@code 範圍 [0 ~ 100]}  @return 不透明度 Opacity {@code [Int]}*/
    public final int getAlpha(){ return alpha; }

    /** 取得 圖片翻轉  @return {@code [0] 水平翻轉} | {@code [1] 垂直翻轉} {@code [boolean[]]}*/
    public final boolean[] getFilp(){ return new boolean[]{ filpH, filpV }; }

}