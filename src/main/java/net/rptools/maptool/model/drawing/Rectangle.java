/*
 * This software Copyright by the RPTools.net development team, and
 * licensed under the Affero GPL Version 3 or, at your option, any later
 * version.
 *
 * MapTool Source Code is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU Affero General Public
 * License * along with this source Code.  If not, please visit
 * <http://www.gnu.org/licenses/> and specifically the Affero license
 * text at <http://www.gnu.org/licenses/agpl.html>.
 */
package net.rptools.maptool.model.drawing;

import com.google.protobuf.StringValue;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import javax.annotation.Nonnull;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.server.Mapper;
import net.rptools.maptool.server.proto.drawing.DrawableDto;
import net.rptools.maptool.server.proto.drawing.RectangleDrawableDto;

/** An rectangle */
public class Rectangle extends AbstractDrawing {
  protected Point startPoint;
  protected Point endPoint;
  private transient java.awt.Rectangle bounds;

  public Rectangle(GUID id, int startX, int startY, int endX, int endY) {
    super(id);
    startPoint = new Point(startX, startY);
    endPoint = new Point(endX, endY);
  }

  public Rectangle(int startX, int startY, int endX, int endY) {
    startPoint = new Point(startX, startY);
    endPoint = new Point(endX, endY);
  }

  @Override
  public @Nonnull Area getArea(Zone zone) {
    return new Area(getBounds(zone));
  }

  @Override
  public DrawableDto toDto() {
    var dto =
        RectangleDrawableDto.newBuilder()
            .setId(getId().toString())
            .setLayer(getLayer().name())
            .setStartPoint(Mapper.map(getStartPoint()))
            .setEndPoint(Mapper.map(getEndPoint()));

    if (getName() != null) dto.setName(StringValue.of(getName()));

    return DrawableDto.newBuilder().setRectangleDrawable(dto).build();
  }

  @Override
  public java.awt.Rectangle getBounds(Zone zone) {
    if (bounds == null) {
      int x = Math.min(startPoint.x, endPoint.x);
      int y = Math.min(startPoint.y, endPoint.y);
      int width = Math.abs(endPoint.x - startPoint.x);
      int height = Math.abs(endPoint.y - startPoint.y);

      bounds = new java.awt.Rectangle(x, y, width, height);
    }
    return bounds;
  }

  public Point getStartPoint() {
    return startPoint;
  }

  public Point getEndPoint() {
    return endPoint;
  }

  @Override
  protected void draw(Zone zone, Graphics2D g) {
    int minX = Math.min(startPoint.x, endPoint.x);
    int minY = Math.min(startPoint.y, endPoint.y);

    int width = Math.abs(startPoint.x - endPoint.x);
    int height = Math.abs(startPoint.y - endPoint.y);

    Object oldAA = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    g.drawRect(minX, minY, width, height);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);
  }

  @Override
  protected void drawBackground(Zone zone, Graphics2D g) {
    int minX = Math.min(startPoint.x, endPoint.x);
    int minY = Math.min(startPoint.y, endPoint.y);

    int width = Math.abs(startPoint.x - endPoint.x);
    int height = Math.abs(startPoint.y - endPoint.y);

    Object oldAA = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    g.fillRect(minX, minY, width, height);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);
  }
}
