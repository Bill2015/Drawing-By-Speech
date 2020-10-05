package talkdraw.componet.menuitem;

/** <p>建構可以修改的 {@link #TextSliderMenuItem}</p> 
 *  <p>滑鼠快速點擊兩下時，就可以修改裡面的值</p>
 *  <p>註：建構後一定要呼叫下列 Function 來使建構完整</p>
 *  <blockquote><pre> 
 *   //建構所需的函式
 *   setExecute()  setValue()  setRange()  build()
 *  </pre></blockquote> */
public class TextSliderMenuItemBuilder {
    private int minVal = 0, maxVal = 100, value = 50;
    private String title;
    /** 建構子 
     *  底下這些是必要設定的
     *   <blockquote><pre> //建構所需的函式
     *   setExecute()  setValue()  setRange()  build() </pre></blockquote>
     *  @param title 標題 */
    public TextSliderMenuItemBuilder(String title){
        this.title = title;
    }
    /** <p>設定此 TextSliderMenuItem 的初始值</p> 
     *  @param value 欲設定的數值 */
    public TextSliderMenuItemBuilder setValue( int value ){
        this.value = value;
        return this;
    }
    /** <p>設定此 TextSliderMenuItem 的範圍</p> 
     *  @param minVal 最小值 
     *  @param maxVal 最大值*/
    public TextSliderMenuItemBuilder setRange( int minVal, int maxVal ){
        this.minVal = minVal;
        this.maxVal = maxVal;
        return this;
    }
    /** 建構完成 
     *  @return 回傳建構完成的 TextSliderMenuItem {@code [TextSliderMenuItem]}*/
    public TextSliderMenuItem build(){ return new TextSliderMenuItem(title, minVal, maxVal, value); }
}