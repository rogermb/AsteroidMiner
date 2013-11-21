package ch.kanti_wohlen.asteroidminer.entities.asteroids;

import ch.kanti_wohlen.asteroidminer.TaskScheduler;
import ch.kanti_wohlen.asteroidminer.Textures;
import ch.kanti_wohlen.asteroidminer.entities.Damageable;
import ch.kanti_wohlen.asteroidminer.entities.Entity;
import ch.kanti_wohlen.asteroidminer.entities.EntityType;
import ch.kanti_wohlen.asteroidminer.entities.sub.HealthBar;
import ch.kanti_wohlen.asteroidminer.powerups.PowerUpLauncher;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class IceAsteroid extends Entity implements Damageable {

	public static final int MAX_HEALTH = 75;
	public static final float MIN_RADIUS = 0.5f;
	private static final float POWER_UP_SPAWN_CHANCE = 0.2f;

	private final HealthBar healthBar;
	private final Fixture circleFixture;
	private final float firstRadius;

	private float currentRadius;
	private float renderScale;
	private int health;

	public IceAsteroid(World world, Vector2 location, float radius) {
		this(world, location, radius, null);
	}

	public IceAsteroid(World world, Vector2 location, float radius, Vector2 velocity) {
		super(world, createBodyDef(location, velocity), createCircle(radius));
		healthBar = new HealthBar(MAX_HEALTH);
		circleFixture = getPhysicsBody().getFixtureList().get(0);
		firstRadius = radius;
		currentRadius = radius;
		renderScale = (radius * BOX2D_TO_PIXEL * 2f) / Textures.ASTEROID.getRegionWidth();
		health = MAX_HEALTH;
	}

	@Override
	public void render(SpriteBatch batch) {
		Sprite s = Textures.ASTEROID;
		positionSprite(s);
		s.setScale(renderScale);
		s.draw(batch);

		healthBar.render(batch, health, new Vector2(s.getX() + s.getWidth() * 0.025f, s.getY() + s.getHeight() * 1.15f));
	}

	@Override
	public boolean isRemoved() {
		return super.isRemoved() || health == 0;
	}

	@Override
	public EntityType getType() {
		return EntityType.ASTEROID;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int newHealth) {
		if (newHealth != health) {
			health = MathUtils.clamp(newHealth, 0, MAX_HEALTH);
			healthBar.resetAlpha();

			if (health == 0) {
				if (MathUtils.random() > POWER_UP_SPAWN_CHANCE) return;
				final World world = getPhysicsBody().getWorld();
				final Vector2 loc = getPhysicsBody().getPosition();
				PowerUpLauncher pul = new PowerUpLauncher(world, loc);
				TaskScheduler.INSTANCE.runTask(pul);
			} else {
				currentRadius = MIN_RADIUS + ((float) health / MAX_HEALTH) * (firstRadius - MIN_RADIUS);
				renderScale = (currentRadius * BOX2D_TO_PIXEL * 2f) / Textures.ASTEROID.getRegionWidth();
				circleFixture.getShape().setRadius(currentRadius);
				getPhysicsBody().resetMassData();
			}
		}
	}

	public void heal(int healingAmoung) {
		setHealth(health + healingAmoung);
	}

	public void damage(int damageAmount) {
		setHealth(health - damageAmount);
	}

	public void kill() {
		setHealth(0);
	}

	private static BodyDef createBodyDef(Vector2 position, Vector2 velocity) {
		final BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(position);
		bodyDef.angle = MathUtils.random(2 * MathUtils.PI);
		if (velocity != null) {
			bodyDef.linearVelocity.set(velocity);
		}
		bodyDef.gravityScale = 0.1f;

		return bodyDef;
	}

	private static FixtureDef createCircle(float radius) {
		final FixtureDef fixture = new FixtureDef();
		fixture.density = 100f;
		fixture.restitution = 0.9f;
		final CircleShape cs = new CircleShape();
		cs.setRadius(radius);
		fixture.shape = cs;
		return fixture;
	}
}
