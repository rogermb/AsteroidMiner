package ch.kanti_wohlen.asteroidminer.entities;

import ch.kanti_wohlen.asteroidminer.Textures;
import ch.kanti_wohlen.asteroidminer.screen.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Laser extends Entity {

	private static final float SPEED = 60f;
	private final SpaceShip ship;

	public Laser(World world, SpaceShip spaceShip) {
		super(world, createBodyDef(spaceShip), createFixture());
		ship = spaceShip;
	}

	@Override
	public void render(SpriteBatch batch) {
		Sprite s = Textures.LASER;
		positionSprite(s);
		s.draw(batch);
	}

	@Override
	public boolean isRemoved() {
		if (super.isRemoved()) return true;
		Vector2 loc = getPhysicsBody().getPosition();
		float w = Gdx.graphics.getWidth() * PIXEL_TO_BOX2D;
		float h = Gdx.graphics.getHeight() * PIXEL_TO_BOX2D;
		return Math.abs(loc.x) > GameScreen.WORLD_SIZE + w || Math.abs(loc.y) > GameScreen.WORLD_SIZE + h;
	}

	@Override
	public EntityType getType() {
		return EntityType.LASER;
	}

	public SpaceShip getShooter() {
		return ship;
	}

	private static BodyDef createBodyDef(SpaceShip ship) {
		BodyDef bd = new BodyDef();
		bd.allowSleep = false;
		bd.type = BodyType.KinematicBody;
		bd.fixedRotation = true;
		bd.gravityScale = 0f;

		final Body body = ship.getPhysicsBody();
		final float x = -MathUtils.sin(body.getAngle());
		final float y = MathUtils.cos(body.getAngle());
		final float h = Textures.SPACESHIP.getHeight() * 0.5f * PIXEL_TO_BOX2D;

		Vector2 pos = new Vector2(body.getPosition());
		pos.add(h * x, h * y);
		bd.position.set(pos);
		bd.linearVelocity.set(x * SPEED, y * SPEED);
		bd.linearVelocity.add(body.getLinearVelocity());
		bd.angle = body.getAngle();

		return bd;
	}

	private static FixtureDef createFixture() {
		final FixtureDef fixture = new FixtureDef();
		fixture.density = 0f;
		fixture.isSensor = true;
		final PolygonShape ps = new PolygonShape();
		ps.setAsBox(Textures.LASER.getWidth() / 4f * PIXEL_TO_BOX2D, Textures.LASER.getHeight() / 4f * PIXEL_TO_BOX2D);
		fixture.shape = ps;
		return fixture;
	}
}
