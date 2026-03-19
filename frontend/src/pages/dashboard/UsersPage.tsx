import { useState, useEffect } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger, DialogClose } from "@/components/ui/dialog";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Plus, Pencil, Trash2, Search, Loader2 } from "lucide-react";
import { toast } from "sonner";
import { userService, User } from "@/services/user.service";
import { roleService, Role } from "@/services/role.service";

export default function UsersPage() {
  const [loading, setLoading] = useState(true);
  const [users, setUsers] = useState<User[]>([]);
  const [roles, setRoles] = useState<Role[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [pagination, setPagination] = useState({ page: 1, total: 0, pages: 1 });

  // Form states
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [newUserName, setNewUserName] = useState("");
  const [newUserEmail, setNewUserEmail] = useState("");
  const [newUserPassword, setNewUserPassword] = useState("");
  const [newUserRole, setNewUserRole] = useState<string>("");

  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [editName, setEditName] = useState("");
  const [editEmail, setEditEmail] = useState("");
  const [editRoleId, setEditRoleId] = useState<number>(0);

  useEffect(() => {
    fetchData();
  }, [pagination.page]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [usersData, rolesData] = await Promise.all([
        userService.getAllUsers(pagination.page),
        roleService.getAllRoles(1, 100)
      ]);
      setUsers(usersData.results);
      setPagination({
        page: usersData.meta.page,
        total: usersData.meta.total,
        pages: usersData.meta.pages
      });
      setRoles(rolesData.results);
    } catch (error) {
      toast.error("Failed to load users data");
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async () => {
    if (!newUserName || !newUserEmail || !newUserPassword || !newUserRole) {
      toast.error("Please fill all fields");
      return;
    }
    try {
      await userService.createUser({
        name: newUserName,
        email: newUserEmail,
        password: newUserPassword,
        roleId: parseInt(newUserRole),
        age: 20, // default or add to form
        gender: "MALE" // default or add to form
      });
      toast.success("User created successfully");
      setIsCreateOpen(false);
      fetchData();
      // Reset form
      setNewUserName("");
      setNewUserEmail("");
      setNewUserPassword("");
      setNewUserRole("");
    } catch (error) {
      toast.error("Failed to create user");
    }
  };

  const handleUpdate = async () => {
    if (!editingUser) return;
    try {
      await userService.updateUser({
        id: editingUser.id,
        name: editName,
        email: editEmail,
        roleId: editRoleId,
        age: editingUser.age || 20,
        gender: editingUser.gender || "MALE"
      });
      toast.success("User updated successfully");
      setEditingUser(null);
      fetchData();
    } catch (error) {
      toast.error("Failed to update user");
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await userService.deleteUser(id);
      toast.success("User deleted successfully");
      fetchData();
    } catch (error) {
      toast.error("Failed to delete user");
    }
  };

  const filteredUsers = users.filter(
    (u) =>
      u.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.role?.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="max-w-6xl mx-auto p-4">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-3xl font-serif font-bold text-slate-900 dark:text-slate-50">User Management</h1>
          <p className="text-slate-500 mt-1">Manage system access and permissions</p>
        </div>
        
        <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
          <DialogTrigger asChild>
            <Button className="gap-2 bg-indigo-600 hover:bg-indigo-700 shadow-md">
              <Plus className="w-4 h-4" /> Add User
            </Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-[425px]">
            <DialogHeader>
              <DialogTitle className="font-serif text-xl border-b pb-2">Create New User</DialogTitle>
            </DialogHeader>
            <div className="space-y-4 pt-4">
              <div className="space-y-1.5">
                <Label className="text-sm font-semibold">Full Name</Label>
                <Input placeholder="John Doe" value={newUserName} onChange={e => setNewUserName(e.target.value)} />
              </div>
              <div className="space-y-1.5">
                <Label className="text-sm font-semibold">Email Address</Label>
                <Input type="email" placeholder="john@university.edu" value={newUserEmail} onChange={e => setNewUserEmail(e.target.value)} />
              </div>
              <div className="space-y-1.5">
                <Label className="text-sm font-semibold">Security Password</Label>
                <Input type="password" placeholder="••••••••" value={newUserPassword} onChange={e => setNewUserPassword(e.target.value)} />
              </div>
              <div className="space-y-1.5">
                <Label className="text-sm font-semibold">System Role</Label>
                <Select onValueChange={setNewUserRole} value={newUserRole}>
                  <SelectTrigger className="w-full">
                    <SelectValue placeholder="Select a role" />
                  </SelectTrigger>
                  <SelectContent>
                    {roles.map(role => (
                      <SelectItem key={role.id} value={role.id.toString()}>
                        {role.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="pt-4 flex gap-3">
                <Button variant="outline" className="flex-1" onClick={() => setIsCreateOpen(false)}>Cancel</Button>
                <Button className="flex-1 bg-indigo-600" onClick={handleCreate}>Create Account</Button>
              </div>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      {/* Search & Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div className="md:col-span-3 relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
          <Input
            className="pl-10 bg-white border-slate-200"
            placeholder="Search users by name, email, or role..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
        <Card className="flex items-center justify-center bg-indigo-50 border-indigo-100 p-2 text-indigo-700 font-medium">
          Total Users: {pagination.total}
        </Card>
      </div>

      <Card className="shadow-xl border-0 overflow-hidden bg-white/50 backdrop-blur-sm">
        <Table>
          <TableHeader className="bg-slate-50">
            <TableRow>
              <TableHead className="font-bold text-slate-700">Name</TableHead>
              <TableHead className="font-bold text-slate-700">Email</TableHead>
              <TableHead className="font-bold text-slate-700">Role</TableHead>
              <TableHead className="font-bold text-slate-700">Account ID</TableHead>
              <TableHead className="w-28 text-right pr-6 font-bold text-slate-700">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={5} className="text-center py-20">
                  <div className="flex flex-col items-center gap-2">
                    <Loader2 className="w-8 h-8 animate-spin text-indigo-500" />
                    <p className="text-slate-500">Retrieving users database...</p>
                  </div>
                </TableCell>
              </TableRow>
            ) : filteredUsers.length === 0 ? (
              <TableRow>
                <TableCell colSpan={5} className="text-center text-slate-500 py-12">
                  No users matched your search criteria.
                </TableCell>
              </TableRow>
            ) : (
              filteredUsers.map((u) => (
                <TableRow key={u.id} className="hover:bg-slate-50/80 transition-colors">
                  <TableCell className="font-semibold text-slate-900">{u.name}</TableCell>
                  <TableCell className="text-slate-600">{u.email}</TableCell>
                  <TableCell>
                    <span className={`capitalize text-[10px] tracking-wider px-2.5 py-1 rounded-full font-bold shadow-sm ${
                      u.role?.name === "ADMIN" ? "bg-rose-100 text-rose-700" :
                      u.role?.name === "LECTURER" ? "bg-amber-100 text-amber-700" :
                      "bg-emerald-100 text-emerald-700"
                    }`}>
                      {u.role?.name || "No Role"}
                    </span>
                  </TableCell>
                  <TableCell className="font-mono text-xs text-slate-400">#USR-{u.id}</TableCell>
                  <TableCell className="text-right pr-4">
                    <div className="flex gap-1 justify-end">
                      {/* Edit Dialog */}
                      <Dialog 
                        open={editingUser?.id === u.id} 
                        onOpenChange={(open) => {
                          if (open) {
                            setEditingUser(u);
                            setEditName(u.name);
                            setEditEmail(u.email);
                            setEditRoleId(u.role?.id || 0);
                          } else {
                            setEditingUser(null);
                          }
                        }}
                      >
                        <DialogTrigger asChild>
                          <Button variant="ghost" size="icon" className="h-8 w-8 text-indigo-600 hover:bg-indigo-50">
                            <Pencil className="w-4 h-4" />
                          </Button>
                        </DialogTrigger>
                        <DialogContent className="sm:max-w-[400px]">
                          <DialogHeader>
                            <DialogTitle className="font-serif border-b pb-2">Edit User Profile</DialogTitle>
                          </DialogHeader>
                          <div className="space-y-4 pt-4">
                            <div className="space-y-1.5">
                              <Label className="text-sm font-semibold">Full Name</Label>
                              <Input value={editName} onChange={e => setEditName(e.target.value)} />
                            </div>
                            <div className="space-y-1.5">
                              <Label className="text-sm font-semibold">Email Address</Label>
                              <Input value={editEmail} onChange={e => setEditEmail(e.target.value)} />
                            </div>
                            <div className="space-y-1.5">
                              <Label className="text-sm font-semibold">Change Role</Label>
                              <Select onValueChange={(v) => setEditRoleId(parseInt(v))} value={editRoleId.toString()}>
                                <SelectTrigger>
                                  <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                  {roles.map(role => (
                                    <SelectItem key={role.id} value={role.id.toString()}>
                                      {role.name}
                                    </SelectItem>
                                  ))}
                                </SelectContent>
                              </Select>
                            </div>
                            <div className="pt-4 flex gap-3">
                              <Button variant="outline" className="flex-1" onClick={() => setEditingUser(null)}>Cancel</Button>
                              <Button className="flex-1 bg-indigo-600 shadow-md" onClick={handleUpdate}>Save Changes</Button>
                            </div>
                          </div>
                        </DialogContent>
                      </Dialog>

                      {/* Delete */}
                      <Dialog>
                        <DialogTrigger asChild>
                          <Button variant="ghost" size="icon" className="h-8 w-8 text-rose-500 hover:bg-rose-50">
                            <Trash2 className="w-4 h-4" />
                          </Button>
                        </DialogTrigger>
                        <DialogContent>
                          <DialogHeader>
                            <DialogTitle className="font-serif text-rose-600">Delete Account</DialogTitle>
                          </DialogHeader>
                          <div className="py-2">
                             <p className="text-slate-600 text-sm">
                              Are you sure you want to permanently delete the account for <span className="font-bold text-slate-900">{u.name}</span>?
                            </p>
                            <div className="mt-4 p-3 bg-rose-50 rounded-lg border border-rose-100">
                              <p className="text-[11px] text-rose-700 font-medium">WARNING: All submissions, grades, and records associated with this user will be disconnected. This action is irreversible.</p>
                            </div>
                          </div>
                          <div className="flex gap-3 justify-end pt-4">
                            <DialogClose asChild>
                              <Button variant="outline" size="sm">Cancel</Button>
                            </DialogClose>
                            <DialogClose asChild>
                              <Button variant="destructive" size="sm" onClick={() => handleDelete(u.id)}>Confirm Deletion</Button>
                            </DialogClose>
                          </div>
                        </DialogContent>
                      </Dialog>
                    </div>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
        
        {/* Pagination placeholder */}
        {!loading && pagination.pages > 1 && (
          <div className="p-4 bg-slate-50 flex items-center justify-center gap-2 border-t">
            <Button 
              variant="outline" 
              size="sm" 
              disabled={pagination.page <= 1}
              onClick={() => setPagination(prev => ({ ...prev, page: prev.page - 1 }))}
            >
              Previous
            </Button>
            <span className="text-xs font-medium text-slate-500">
              Page {pagination.page} of {pagination.pages}
            </span>
            <Button 
              variant="outline" 
              size="sm" 
              disabled={pagination.page >= pagination.pages}
              onClick={() => setPagination(prev => ({ ...prev, page: prev.page + 1 }))}
            >
              Next
            </Button>
          </div>
        )}
      </Card>
    </div>
  );
}
