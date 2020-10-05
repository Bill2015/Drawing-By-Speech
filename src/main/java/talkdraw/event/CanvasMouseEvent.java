package talkdraw.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;

/** <p>繼承了 {@link Event}<p> 
 *  <p>提供能在 畫布{@code Cavnas} 點擊物件而產生的事件{@link Event}類別</p>
 *  <p>會需要這個是因為在原本的傳遞 {@link MouseEvent} 中，滑鼠的座標會產生位移</p>
 *  <p>所以需要去紀錄在 {@code 畫布} 上的座標位置，在傳遞到 {@code ClickableImageView} 裡做處裡</p>
 *  <p>註：假如要取得在 {@code 畫布} 上的座標位置，請使用 
 * <pre>
 *public double getX();
 *public double getY();
 * </pre>
 *  @see talkdraw.componet.ClickableImageView*/
public class CanvasMouseEvent extends Event {

    /** 在畫布上基礎的事件 */
    public static final EventType<CanvasMouseEvent> ANY = new EventType<>(Event.ANY, "CAVASN_MOUSE_ANY");

    /** 在畫布上點擊的 X, Y 座標 */
    private final double cavansX, cavansY;
    /** 滑鼠事件 */
    private final MouseEvent mouseEvent;
    /** 判斷在畫布上，滑鼠座標是否在物件上 */
    private final boolean isOnView;

    /** 建構子 
     *  @param mouseEvent 滑鼠事件
     *  @param isOnView 當事件發生時，是否在物件上*/
    public CanvasMouseEvent( MouseEvent mouseEvent, boolean isOnView){
        super( ANY );
        this.cavansX = mouseEvent.getX();
        this.cavansY = mouseEvent.getY();
        this.mouseEvent = mouseEvent;
        this.isOnView = isOnView;
    }

    /** 取得滑鼠事件 {@link MouseEvent}
     *  @return 滑鼠事件 {@code [MouseEvent]}*/
    public MouseEvent getMouseEvent(){
        return mouseEvent;
    }
    /** 取得事件觸發時在 {@code 畫布} 上的 {@code X 座標}
     *  @return X 座標 {@code [Double]}*/
    public double getX(){
        return cavansX;
    }
    /** 取得事件觸發時在 {@code 畫布} 上的 {@code Y 座標}
     *  @return Y 座標 {@code [Double]}*/
    public double getY(){
        return cavansY;
    }
    /** 取得事件觸發時在 {@code 畫布} 上的滑鼠是否在物件上
     *  @return 是否在物件上 {@code [Boolean]}*/
    public boolean isOnView(){
        return isOnView;
    }
}