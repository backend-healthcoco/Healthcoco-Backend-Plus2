// mongeez formatted javascript
// changeset system:v1_8
db.role_cl.insert({
	"role" : "HOSPITAL_ADMIN",
	"discarded" : false
});
db.role_cl.insert({
	"role" : "LOCATION_ADMIN",
	"discarded" : false
});
db.role_cl.insert({
	"role" : "DOCTOR",
	"discarded" : false
});

db.role_cl.insert({
	"_id" : ObjectId("5794af08e4b01f1d73f9b7c5"),
	"_class" : "com.dpdocter.collections.RoleCollection",
	"role" : "HOSPITAL_ADMIN",
	"locationId" : ObjectId("5794af08e4b01f1d73f9b7c3"),
	"hospitalId" : ObjectId("5794af08e4b01f1d73f9b7c2"),
	"discarded" : false,
	"createdTime" : ISODate("2016-07-24T12:05:28.825Z"),
	"updatedTime" : ISODate("2016-07-24T12:05:28.825Z")
});

db.role_cl.insert({
	"_id" : ObjectId("5794af08e4b01f1d73f9b7c7"),
	"_class" : "com.dpdocter.collections.RoleCollection",
	"role" : "LOCATION_ADMIN",
	"locationId" : ObjectId("5794af08e4b01f1d73f9b7c3"),
	"hospitalId" : ObjectId("5794af08e4b01f1d73f9b7c2"),
	"discarded" : false,
	"createdTime" : ISODate("2016-07-24T12:05:28.831Z"),
	"updatedTime" : ISODate("2016-07-24T12:05:28.831Z")
});

db.role_cl.insert({
	"_id" : ObjectId("5794af08e4b01f1d73f9b7c9"),
	"_class" : "com.dpdocter.collections.RoleCollection",
	"role" : "DOCTOR",
	"locationId" : ObjectId("5794af08e4b01f1d73f9b7c3"),
	"hospitalId" : ObjectId("5794af08e4b01f1d73f9b7c2"),
	"discarded" : false,
	"createdTime" : ISODate("2016-07-24T12:05:28.837Z"),
	"updatedTime" : ISODate("2016-07-24T12:05:28.837Z")
});
